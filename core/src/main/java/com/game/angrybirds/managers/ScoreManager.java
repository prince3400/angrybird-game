package com.game.angrybirds.managers;

import com.game.angrybirds.levels.BlockType;
import com.game.angrybirds.utils.GameConstants;

/**
 * Tracks and aggregates score during gameplay.
 */
public class ScoreManager {

    private int currentScore;
    private int birdsRemaining;

    public void reset(int birdCount) {
        currentScore = 0;
        birdsRemaining = birdCount;
    }

    public void addPigScore() {
        currentScore += GameConstants.SCORE_PIG;
    }

    public void addBlockScore(BlockType type) {
        switch (type) {
            case WOOD:
                currentScore += GameConstants.SCORE_WOOD_BLOCK;
                break;
            case STONE:
                currentScore += GameConstants.SCORE_STONE_BLOCK;
                break;
            case GLASS:
                currentScore += GameConstants.SCORE_GLASS_BLOCK;
                break;
            default:
                break;
        }
    }

    public void birdUsed() {
        birdsRemaining = Math.max(0, birdsRemaining - 1);
    }

    public void addRemainingBirdBonus() {
        currentScore += birdsRemaining * GameConstants.SCORE_BIRD_REMAINING;
    }

    public int getScore() {
        return currentScore;
    }

    public int getBirdsRemaining() {
        return birdsRemaining;
    }
}
