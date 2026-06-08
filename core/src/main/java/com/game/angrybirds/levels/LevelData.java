package com.game.angrybirds.levels;

import com.badlogic.gdx.utils.Array;

/**
 * Data container for a single level definition.
 * Decouples level layout from runtime entity spawning.
 */
public class LevelData {

    public String name;
    public int index;
    public Array<BirdType> birds = new Array<>();
    public Array<BlockPlacement> blocks = new Array<>();
    public Array<PigPlacement> pigs = new Array<>();

    public static class BlockPlacement {
        public BlockType type;
        public float x;
        public float y;

        public BlockPlacement(BlockType type, float x, float y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }
    }

    public static class PigPlacement {
        public float x;
        public float y;

        public PigPlacement(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
