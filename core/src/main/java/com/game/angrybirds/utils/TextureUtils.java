package com.game.angrybirds.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** Utility methods for combining/processing textures at runtime. */
public class TextureUtils {

    /**
     * Creates a new Texture by drawing base + overlay onto a single canvas.
     *
     * @param base Base texture (will be drawn at (0,0)).
     * @param overlay Overlay texture (drawn at overlayOffsetX/overlayOffsetY).
     * @param overlayOffsetX X offset in pixels for overlay.
     * @param overlayOffsetY Y offset in pixels for overlay.
     */
    public static Texture combineTextures(Texture base, Texture overlay, int overlayOffsetX, int overlayOffsetY) {
        if (base == null) return null;
        if (overlay == null) {
            // Caller can just use base directly; but to keep method safe, create a copy.
            return base;

        }

        int w = base.getWidth();
        int h = base.getHeight();

        // If overlay differs in size, still render into the base canvas.
        Pixmap canvas = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        Pixmap basePm = baseToPixmap(base);
        Pixmap overlayPm = baseToPixmap(overlay);

        // Draw base
        canvas.drawPixmap(basePm, 0, 0);
        // Draw overlay
        canvas.drawPixmap(overlayPm, overlayOffsetX, overlayOffsetY);

        Texture out = new Texture(canvas);
        basePm.dispose();
        overlayPm.dispose();
        canvas.dispose();
        return out;
    }

    private static Pixmap baseToPixmap(Texture texture) {
        // getTextureData() may require sync.
        return texture.getTextureData().consumePixmap();
    }
}

