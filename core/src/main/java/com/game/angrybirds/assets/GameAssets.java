package com.game.angrybirds.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.game.angrybirds.utils.GameConstants;
import com.game.angrybirds.utils.TextureGenerator;

/**
 * Centralized asset loading and lifecycle management.
 * Wraps LibGDX AssetManager and provides procedural fallbacks for textures.
 */
public class GameAssets implements Disposable {

    private final AssetManager manager = new AssetManager();
    private boolean proceduralMode = true;

    // Procedural textures
    public Texture birdRed;
    public Texture birdBomb;
    public Texture birdSpeed;
    public Texture blockWood;
    public Texture blockStone;
    public Texture blockGlass;
    public Texture pigTexture;
    public Texture slingshotTexture;
public Texture groundTexture;
    public Texture skyTexture;
    public Texture mainMenuBackground;
    public Texture trajectoryDot;
    public Texture uiButton;

    public BitmapFont font;

    // Sound placeholders (optional file-based loading)
    public Sound sfxLaunch;
    public Sound sfxHit;
    public Sound sfxBreak;
    public Sound sfxPig;
    public Sound sfxWin;
    public Sound sfxLose;
    public Music musicMenu;
    public Music musicGame;

    public void load() {
        generateProceduralAssets();
        loadOptionalFileAssets();
    }

    private void generateProceduralAssets() {
        // Use richer stylized procedural sprites as a fallback.
        birdRed = TextureGenerator.createRedBirdSprite(64);
        birdBomb = TextureGenerator.createBombBirdSprite(64);
        birdSpeed = TextureGenerator.createSpeedBirdSprite(64);

        blockWood = TextureGenerator.createRectTexture(48, 48, new Color(0.6f, 0.4f, 0.2f, 1f));
        blockStone = TextureGenerator.createRectTexture(48, 48, new Color(0.5f, 0.5f, 0.55f, 1f));
        blockGlass = TextureGenerator.createRectTexture(48, 48, new Color(0.6f, 0.85f, 0.95f, 0.8f));

        pigTexture = TextureGenerator.createPigSprite(56);

        // If separate player sprites exist, prefer them over procedural fallbacks.
        // (Images should be placed under the repo-root assets/ directory.)
        Texture redPng = tryLoadTexture("redbird.png");
        if (redPng != null) birdRed = redPng;

        Texture pigPng = tryLoadTexture("pig.png");
        if (pigPng != null) pigTexture = pigPng;

        // Use provided slingshot sprites if present, otherwise fall back to procedural rectangle.
        Texture slingBlack = tryLoadTexture("angrybirds/slingblack.png");
        // Note: repo has slingpart.png + slingshot.png (no slingpart fixture alone).
        Texture sling = tryLoadTexture("angrybirds/slingshot.png");
        if (sling == null) sling = tryLoadTexture("angrybirds/slingpart.png");

        if (sling != null) {
            slingshotTexture = sling;
        } else if (slingBlack != null) {
            slingshotTexture = slingBlack;
        } else {
            slingshotTexture = TextureGenerator.createRectTexture(16, 80, new Color(0.35f, 0.2f, 0.1f, 1f));
        }
        // Use provided ground.png if available, otherwise fall back to procedural green rectangle.
        Texture groundPng = tryLoadTexture("angrybirds/ground.png");
        if (groundPng != null) {
            groundTexture = groundPng;
        } else {
            groundTexture = TextureGenerator.createRectTexture(128, 32, new Color(0.3f, 0.55f, 0.2f, 1f));
        }

        skyTexture = TextureGenerator.createRectTexture(4, 4, new Color(0.53f, 0.81f, 0.98f, 1f));
        mainMenuBackground = tryLoadTexture("angrybirds/background.png");
        if (mainMenuBackground == null) mainMenuBackground = skyTexture;
        trajectoryDot = TextureGenerator.createDotTexture(8);
        uiButton = TextureGenerator.createRectTexture(200, 60, new Color(0.2f, 0.45f, 0.75f, 1f));

        font = new BitmapFont();
        // Make UI text render crisper (less blurry) on main menu.
        font.getData().setScale(1.2f);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }


    private Texture tryLoadTexture(String relativeAssetPath) {
        try {
            if (!Gdx.files.internal(relativeAssetPath).exists()) return null;
            Texture tex = new Texture(Gdx.files.internal(relativeAssetPath));
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return tex;
        } catch (Exception ignored) {
            return null;
        }
    }

    /** Attempts to load file-based assets; falls back silently to procedural mode. */
    private void loadOptionalFileAssets() {
        // File assets can be added under assets/ folder; procedural mode is default
        proceduralMode = true;

        // Procedural (non-empty) fallback SFX so collision/hit audio works even without bundled sound files.
        // These are short synthesized PCM buffers (mono, 16-bit LE).
        if (sfxLaunch == null) sfxLaunch = createProceduralHitLikeSound(0.10f, 820f, 0.35f);
        if (sfxHit == null) sfxHit = createProceduralHitLikeSound(0.08f, 520f, 0.40f);
        if (sfxBreak == null) sfxBreak = createProceduralHitLikeSound(0.14f, 280f, 0.45f);
        if (sfxPig == null) sfxPig = createProceduralHitLikeSound(0.18f, 420f, 0.50f);
        if (sfxWin == null) sfxWin = createProceduralHitLikeSound(0.25f, 740f, 0.45f);
        if (sfxLose == null) sfxLose = createProceduralHitLikeSound(0.25f, 220f, 0.45f);
    }

    /**
     * Procedural (non-empty) fallback SFX for when no bundled audio exists.
     *
     * To avoid API-version differences in LibGDX (raw PCM Sound creation differs across versions),
     * we generate a minimal WAV byte stream in memory and load it via a temp FileHandle.
     */
    private Sound createProceduralHitLikeSound(float durationSeconds, float baseFrequency, float volume) {
        // 16kHz mono PCM16 WAV
        final int sampleRate = 16000;
        final int numSamples = Math.max(1, (int)(durationSeconds * sampleRate));
        byte[] wav = createWavPcm16Mono(numSamples, sampleRate, baseFrequency, volume);

        com.badlogic.gdx.files.FileHandle tmp = com.badlogic.gdx.Gdx.files
                .local("sfx_tmp_" + System.nanoTime() + ".wav");
        tmp.writeBytes(wav, false);

        return Gdx.audio.newSound(tmp);
    }

    private byte[] createWavPcm16Mono(int numSamples, int sampleRate, float baseFrequency, float volume) {
        // WAV header size for PCM16 mono
        int subChunk2Size = numSamples * 2; // 16-bit samples
        int chunkSize = 36 + subChunk2Size;

        byte[] data = new byte[44 + subChunk2Size];
        int o = 0;

        // "RIFF"
        o = writeAscii(data, o, "RIFF");
        o = writeLE32(data, o, chunkSize);
        // "WAVE"
        o = writeAscii(data, o, "WAVE");
        // "fmt "
        o = writeAscii(data, o, "fmt ");
        o = writeLE32(data, o, 16); // Subchunk1Size for PCM
        o = writeLE16(data, o, (short) 1); // AudioFormat PCM
        o = writeLE16(data, o, (short) 1); // NumChannels mono
        o = writeLE32(data, o, sampleRate);
        int byteRate = sampleRate * 2; // mono * 16bit
        o = writeLE32(data, o, byteRate);
        o = writeLE16(data, o, (short) 2); // BlockAlign
        o = writeLE16(data, o, (short) 16); // BitsPerSample

        // "data"
        o = writeAscii(data, o, "data");
        o = writeLE32(data, o, subChunk2Size);

        for (int i = 0; i < numSamples; i++) {
            float t = i / (float) sampleRate;
            float decay = (1f - (i / (float) numSamples));
            float envelope = decay * decay;

            float sine = (float) Math.sin(2f * Math.PI * baseFrequency * t);
            float noise = (float) (Math.random() * 2.0 - 1.0);

            float sample = (sine * 0.75f + noise * 0.25f) * envelope * volume;

            short s = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, sample * Short.MAX_VALUE));

            // PCM16 little-endian
            data[o++] = (byte) (s & 0xFF);
            data[o++] = (byte) ((s >> 8) & 0xFF);
        }

        return data;
    }

    private int writeAscii(byte[] data, int offset, String ascii) {
        byte[] b = ascii.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        System.arraycopy(b, 0, data, offset, b.length);
        return offset + b.length;
    }

    private int writeLE16(byte[] data, int offset, short value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        return offset + 2;
    }

    private int writeLE32(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
        return offset + 4;
    }

    public TextureRegion region(Texture texture) {
        return TextureGenerator.region(texture);
    }

    public AssetManager getManager() {
        return manager;
    }

    public boolean isProceduralMode() {
        return proceduralMode;
    }

    public boolean update() {
        return manager.update();
    }

    public float getProgress() {
        return manager.getProgress();
    }

    @Override
    public void dispose() {
        if (birdRed != null) birdRed.dispose();
        if (birdBomb != null) birdBomb.dispose();
        if (birdSpeed != null) birdSpeed.dispose();
        if (blockWood != null) blockWood.dispose();
        if (blockStone != null) blockStone.dispose();
        if (blockGlass != null) blockGlass.dispose();
        if (pigTexture != null) pigTexture.dispose();
        if (slingshotTexture != null) slingshotTexture.dispose();
        if (groundTexture != null) groundTexture.dispose();
        if (mainMenuBackground != null && mainMenuBackground != skyTexture) mainMenuBackground.dispose();
        if (skyTexture != null) skyTexture.dispose();
        if (trajectoryDot != null) trajectoryDot.dispose();
        if (uiButton != null) uiButton.dispose();
        if (font != null) font.dispose();
        manager.dispose();
    }
}
