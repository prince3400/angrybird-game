package com.game.angrybirds.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
        birdRed = TextureGenerator.createCircleTexture(64, new Color(0.9f, 0.2f, 0.15f, 1f));
        birdBomb = TextureGenerator.createCircleTexture(64, new Color(0.15f, 0.15f, 0.2f, 1f));
        birdSpeed = TextureGenerator.createCircleTexture(64, new Color(0.2f, 0.7f, 0.95f, 1f));
        blockWood = TextureGenerator.createRectTexture(48, 48, new Color(0.6f, 0.4f, 0.2f, 1f));
        blockStone = TextureGenerator.createRectTexture(48, 48, new Color(0.5f, 0.5f, 0.55f, 1f));
        blockGlass = TextureGenerator.createRectTexture(48, 48, new Color(0.6f, 0.85f, 0.95f, 0.8f));
        pigTexture = TextureGenerator.createCircleTexture(56, new Color(0.4f, 0.75f, 0.3f, 1f));
        slingshotTexture = TextureGenerator.createRectTexture(16, 80, new Color(0.35f, 0.2f, 0.1f, 1f));
        groundTexture = TextureGenerator.createRectTexture(128, 32, new Color(0.3f, 0.55f, 0.2f, 1f));
        skyTexture = TextureGenerator.createRectTexture(4, 4, new Color(0.53f, 0.81f, 0.98f, 1f));
        trajectoryDot = TextureGenerator.createDotTexture(8);
        uiButton = TextureGenerator.createRectTexture(200, 60, new Color(0.2f, 0.45f, 0.75f, 1f));
        font = new BitmapFont();
        font.getData().setScale(1.2f);
    }

    /** Attempts to load file-based assets; falls back silently to procedural mode. */
    private void loadOptionalFileAssets() {
        // File assets can be added under assets/ folder; procedural mode is default
        proceduralMode = true;
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
        if (skyTexture != null) skyTexture.dispose();
        if (trajectoryDot != null) trajectoryDot.dispose();
        if (uiButton != null) uiButton.dispose();
        if (font != null) font.dispose();
        manager.dispose();
    }
}
