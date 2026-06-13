package com.game.angrybirds.screens;

import com.badlogic.gdx.utils.Array;
import com.game.angrybirds.levels.BlockType;
import com.game.angrybirds.entities.birds.Bird;

public class GameStateSave {
    // ScoreManager state
    public int currentScore;
    public int birdsRemaining;

    // Entities
    public Array<BlockSave> blocks = new Array<>();
    public Array<PigSave> pigs = new Array<>();
    public Array<BirdSave> birds = new Array<>();

    // LevelManager state
    public int currentBirdIndex;
    public boolean hasAttachedBird;
    public String gameState;

    public static class BlockSave {
        public boolean alive;
        public String type;
        public float durability;
        public float x, y, angle, vx, vy, av;
    }

    public static class PigSave {
        public boolean alive;
        public boolean dying;
        public float x, y, angle, vx, vy, av;
    }

    public static class BirdSave {
        public boolean alive;
        public boolean settled;
        public String launchState;
        public boolean abilityUsed;
        public float health;
        public float x, y, angle, vx, vy, av;
    }
}
