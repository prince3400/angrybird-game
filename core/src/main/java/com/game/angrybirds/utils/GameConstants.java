package com.game.angrybirds.utils;

/**
 * Central configuration for gameplay, physics, and rendering constants.
 * Avoid magic numbers throughout the codebase by referencing this class.
 */
public final class GameConstants {

    private GameConstants() {}

    // --- Display ---
    public static final float WORLD_WIDTH = 1280f;
    public static final float WORLD_HEIGHT = 720f;
    public static final float PPM = 32f; // pixels per meter (Box2D scale)

    // --- Physics ---
    public static final float GRAVITY_Y = -9.8f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;
    public static final float TIME_STEP = 1f / 60f;

    // --- Slingshot ---
    public static final float SLINGSHOT_X = 200f;
    public static final float SLINGSHOT_Y = 180f;
    public static final float SLINGSHOT_MAX_DRAG = 120f;
    public static final float SLINGSHOT_LAUNCH_MULTIPLIER = 18f;
    public static final int TRAJECTORY_DOT_COUNT = 15;
    public static final float TRAJECTORY_DOT_INTERVAL = 0.08f;

    // --- Bird ---
    public static final float BIRD_RADIUS = 24f;
    public static final float BIRD_DENSITY = 1.2f;
    public static final float BIRD_RESTITUTION = 0.35f;
    public static final float BIRD_FRICTION = 0.4f;

    // --- Block ---
    public static final float BLOCK_WIDTH = 48f;
    public static final float BLOCK_HEIGHT = 48f;

    // --- Pig ---
    public static final float PIG_RADIUS = 22f;
    public static final float PIG_DEATH_IMPULSE = 4f;

    // --- Damage thresholds ---
    public static final float MIN_DAMAGE_IMPULSE = 1.5f;
    public static final float BLOCK_DAMAGE_SCALE = 0.5f;
    public static final float PIG_DAMAGE_SCALE = 1.0f;

    // --- Scoring ---
    public static final int SCORE_PIG = 5000;
    public static final int SCORE_WOOD_BLOCK = 500;
    public static final int SCORE_STONE_BLOCK = 1000;
    public static final int SCORE_GLASS_BLOCK = 300;
    public static final int SCORE_BIRD_REMAINING = 10000;

    // --- Gameplay ---
    public static final float BIRD_SETTLE_VELOCITY = 0.5f;
    public static final float BIRD_SETTLE_TIME = 2f;
    public static final float CAMERA_LERP = 0.05f;

    // --- Save ---
    public static final String PREFS_NAME = "angrybirds_save";
    public static final String KEY_HIGH_SCORE = "high_score";
    public static final String KEY_UNLOCKED_LEVELS = "unlocked_levels";

    // --- Collision categories (bit masks) ---
    public static final short CATEGORY_BIRD = 0x0001;
    public static final short CATEGORY_BLOCK = 0x0002;
    public static final short CATEGORY_PIG = 0x0004;
    public static final short CATEGORY_GROUND = 0x0008;
    public static final short CATEGORY_SLINGSHOT = 0x0010;
    public static final short CATEGORY_DEBRIS = 0x0020;

    public static final short MASK_BIRD = CATEGORY_BLOCK | CATEGORY_PIG | CATEGORY_GROUND;
    public static final short MASK_BLOCK = CATEGORY_BIRD | CATEGORY_BLOCK | CATEGORY_PIG | CATEGORY_GROUND | CATEGORY_DEBRIS;
    public static final short MASK_PIG = CATEGORY_BIRD | CATEGORY_BLOCK | CATEGORY_GROUND | CATEGORY_DEBRIS;
    public static final short MASK_GROUND = CATEGORY_BIRD | CATEGORY_BLOCK | CATEGORY_PIG | CATEGORY_DEBRIS;
    public static final short MASK_SLINGSHOT = 0x0000;
    public static final short MASK_DEBRIS = CATEGORY_BLOCK | CATEGORY_PIG | CATEGORY_GROUND;

    /** Converts pixel coordinates to Box2D meters. */
    public static float toMeters(float pixels) {
        return pixels / PPM;
    }

    /** Converts Box2D meters to pixel coordinates. */
    public static float toPixels(float meters) {
        return meters * PPM;
    }
}
