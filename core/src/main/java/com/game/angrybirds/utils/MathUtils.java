package com.game.angrybirds.utils;

import com.badlogic.gdx.math.Vector2;

/**
 * Math helpers for slingshot trajectory and physics calculations.
 */
public final class MathUtils {

    private MathUtils() {}

    private static final Vector2 TEMP = new Vector2();

    /** Clamps drag distance to slingshot maximum. */
    public static Vector2 clampDrag(Vector2 drag, float maxLength) {
        if (drag.len() > maxLength) {
            drag.nor().scl(maxLength);
        }
        return drag;
    }

    /** Computes launch velocity from drag vector (inverse direction, scaled). */
    public static Vector2 computeLaunchVelocity(Vector2 drag) {
        TEMP.set(drag).scl(-GameConstants.SLINGSHOT_LAUNCH_MULTIPLIER / GameConstants.PPM);
        return TEMP.cpy();
    }

    /** Predicts trajectory point at time t given initial velocity and gravity. */
    public static Vector2 predictPosition(Vector2 origin, Vector2 velocity, float t) {
        float x = origin.x + velocity.x * t * GameConstants.PPM;
        float y = origin.y + (velocity.y * t + 0.5f * GameConstants.GRAVITY_Y * t * t) * GameConstants.PPM;
        return new Vector2(x, y);
    }
}
