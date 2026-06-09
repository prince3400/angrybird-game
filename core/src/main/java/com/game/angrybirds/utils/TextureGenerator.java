package com.game.angrybirds.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Generates procedural (original) cartoon-like textures when external assets are unavailable.
 * This keeps the game playable without bundled image files.
 *
 * Note: These are not copies of copyrighted Angry Birds art; they are stylized procedural drawings.
 */
public final class TextureGenerator {

    private TextureGenerator() {}

    public static Texture createCircleTexture(int size, Color color) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.CLEAR);
        pixmap.fill();
        pixmap.setColor(color);
        pixmap.fillCircle(size / 2, size / 2, size / 2 - 2);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    public static Texture createRectTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);

        // subtle border for depth
        pixmap.setColor(color.cpy().mul(0.7f));
        pixmap.drawRectangle(0, 0, width - 1, height - 1);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    public static Texture createDotTexture(int size) {
        return createCircleTexture(size, new Color(1f, 1f, 1f, 0.6f));
    }

    /**
     * Simple “cartoon” red bird sprite: rounded head/body, wing shading, eye + beak.
     */
    public static Texture createRedBirdSprite(int size) {
        return createBirdSprite(size,
                new Color(0.92f, 0.20f, 0.18f, 1f),
                new Color(0.64f, 0.10f, 0.10f, 1f),
                new Color(0.10f, 0.10f, 0.10f, 1f),
                new Color(0.98f, 0.78f, 0.20f, 1f),
                new Color(0.98f, 0.98f, 0.98f, 1f),
                false);
    }

    /**
     * Bomb bird sprite: darker body + small “bomb” motif on top.
     */
    public static Texture createBombBirdSprite(int size) {
        return createBirdSprite(size,
                new Color(0.18f, 0.18f, 0.27f, 1f),
                new Color(0.10f, 0.10f, 0.16f, 1f),
                new Color(0.08f, 0.08f, 0.08f, 1f),
                new Color(1.0f, 0.85f, 0.25f, 1f),
                new Color(0.98f, 0.98f, 0.98f, 1f),
                true);
    }

    /**
     * Speed bird sprite: teal/blue-green body with subtle streak stripe.
     */
    public static Texture createSpeedBirdSprite(int size) {
        return createBirdSprite(size,
                new Color(0.18f, 0.70f, 0.93f, 1f),
                new Color(0.10f, 0.45f, 0.62f, 1f),
                new Color(0.08f, 0.08f, 0.08f, 1f),
                new Color(1.0f, 0.85f, 0.25f, 1f),
                new Color(0.98f, 0.98f, 0.98f, 1f),
                false);
    }

    /**
     * Stylized pig sprite: rounded face with snout highlight + eye.
     */
    public static Texture createPigSprite(int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.CLEAR);
        pixmap.fill();

        int cx = size / 2;
        int cy = size / 2;

        Color body = new Color(0.45f, 0.78f, 0.32f, 1f);
        Color shadow = new Color(0.25f, 0.54f, 0.18f, 1f);
        Color highlight = new Color(0.70f, 0.98f, 0.58f, 1f);
        Color eyeWhite = new Color(0.98f, 0.98f, 0.98f, 1f);
        Color pupil = new Color(0.06f, 0.06f, 0.06f, 1f);
        Color nostril = new Color(0.08f, 0.08f, 0.08f, 1f);

        int radius = size / 2 - 2;

        // body
        pixmap.setColor(body);
        pixmap.fillCircle(cx, cy, radius);

        // shadow (bottom-left)
        pixmap.setColor(shadow);
        pixmap.fillCircle(cx - size / 10, cy + size / 12, radius - size / 12);

        // highlight (top-right)
        pixmap.setColor(highlight);
        pixmap.fillCircle(cx + size / 10, cy - size / 12, radius - size / 10);

        // snout (Pixmap has no fillOval; draw it by scanline ellipse)
        int snoutW = (int) (size * 0.35f);
        int snoutH = (int) (size * 0.30f);
        int snoutX = cx - snoutW / 2;
        int snoutY = cy + size / 10;

        Color snout = new Color(body.r * 0.96f, body.g * 0.96f, body.b * 0.96f, 1f);
        pixmap.setColor(snout);

        // ellipse scanline fill
        int a = snoutW / 2; // x radius
        int b = snoutH / 2; // y radius
        int centerX = snoutX + a;
        int centerY = snoutY + b;

        for (int yy = -b; yy <= b; yy++) {
            float t = 1f - (yy * yy) / (float) (b * b);
            if (t < 0f) continue;
            int xx = (int) (a * Math.sqrt(t));
            pixmap.drawLine(centerX - xx, centerY + yy, centerX + xx, centerY + yy);
        }

        // nostrils
        pixmap.setColor(nostril);
        int n1x = cx - snoutW / 6;
        int n2x = cx + snoutW / 6;
        int nY = snoutY + snoutH / 2 - size / 40;
        pixmap.fillCircle(n1x, nY, Math.max(2, size / 24));
        pixmap.fillCircle(n2x, nY, Math.max(2, size / 24));

        // eye
        int eyeX = cx + size / 10;
        int eyeY = cy - size / 8;
        pixmap.setColor(eyeWhite);
        pixmap.fillCircle(eyeX, eyeY, Math.max(2, size / 18));
        pixmap.setColor(pupil);
        pixmap.fillCircle(eyeX + size / 40, eyeY - size / 40, Math.max(1, size / 30));

        // outline-ish depth: thin darker rim
        pixmap.setColor(shadow.cpy().mul(0.9f));
        int rim = Math.max(1, size / 16);
        pixmap.fillCircle(cx, cy, radius - rim);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    public static TextureRegion region(Texture texture) {
        return new TextureRegion(texture);
    }

    private static Texture createBirdSprite(
            int size,
            Color body,
            Color shadow,
            Color pupilColor,
            Color beakColor,
            Color eyeWhite,
            boolean bombMotif) {

        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.CLEAR);
        pixmap.fill();

        int cx = size / 2;
        int cy = size / 2;
        int radius = size / 2 - 2;

        // main body
        pixmap.setColor(body);
        pixmap.fillCircle(cx, cy, radius);

        // wing / shading
        pixmap.setColor(shadow);
        pixmap.fillCircle(cx - size / 10, cy + size / 10, radius - size / 10);

        // highlight
        pixmap.setColor(body.cpy().mul(1.05f));
        pixmap.fillCircle(cx + size / 10, cy - size / 10, radius - size / 14);

        // optional bomb cap / top motif
        if (bombMotif) {
            Color cap = new Color(0.08f, 0.08f, 0.12f, 1f);
            int capR = Math.max(8, size / 6);
            pixmap.setColor(cap);
            pixmap.fillCircle(cx, cy - size / 8, capR);

            Color capHi = new Color(0.20f, 0.20f, 0.30f, 1f);
            pixmap.setColor(capHi);
            pixmap.fillCircle(cx - size / 20, cy - size / 10, capR - Math.max(2, size / 14));

            // small fuse-ish lines
            pixmap.setColor(beakColor.cpy().mul(0.75f));
            pixmap.drawLine(cx, cy - size / 8, cx, cy - size / 2 + size / 20);
        } else {
            // speed stripe motif (stylized)
            if (Math.abs(body.g - 0.70f) < 0.35f) {
                pixmap.setColor(new Color(1f, 1f, 1f, 0.18f));
                int x0 = cx - size / 6;
                int y0 = cy - size / 10;
                int w = size / 3;
                int h = size / 10;
                pixmap.fillRectangle(x0, y0, w, h);
                pixmap.setColor(new Color(0f, 0f, 0f, 0.12f));
                pixmap.fillRectangle(x0 + size / 12, y0 + size / 8, w / 2, h / 2);
            }
        }

        // eye
        int eyeX = cx + size / 10;
        int eyeY = cy - size / 8;
        int eyeR = Math.max(2, size / 18);
        pixmap.setColor(eyeWhite);
        pixmap.fillCircle(eyeX, eyeY, eyeR);

        pixmap.setColor(pupilColor);
        pixmap.fillCircle(eyeX + size / 40, eyeY - size / 40, Math.max(1, size / 30));

        // beak (triangle-ish)
        int beakW = Math.max(6, size / 10);
        int beakH = Math.max(5, size / 14);
        int beakX = cx + size / 2 - size / 8;
        int beakY = cy + size / 40;

        pixmap.setColor(beakColor);
        pixmap.fillTriangle(
                beakX - beakW, beakY - beakH / 2,
                beakX, beakY,
                beakX - beakW, beakY + beakH / 2);

        // subtle rim for depth
        pixmap.setColor(shadow.cpy().mul(0.95f));
        int rimR = radius - Math.max(1, size / 18);
        pixmap.fillCircle(cx, cy, rimR);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }
}
