package com.game.angrybirds.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Persists high scores and unlocked level progress using LibGDX Preferences.
 */
public class SaveManager {

    private final Preferences prefs;

    public SaveManager() {
        prefs = Gdx.app.getPreferences(com.game.angrybirds.utils.GameConstants.PREFS_NAME);
    }

    public int getHighScore() {
        return prefs.getInteger(com.game.angrybirds.utils.GameConstants.KEY_HIGH_SCORE, 0);
    }

    public void setHighScore(int score) {
        if (score > getHighScore()) {
            prefs.putInteger(com.game.angrybirds.utils.GameConstants.KEY_HIGH_SCORE, score);
            prefs.flush();
        }
    }

    public int getUnlockedLevels() {
        return prefs.getInteger(com.game.angrybirds.utils.GameConstants.KEY_UNLOCKED_LEVELS, 1);
    }

    public void unlockLevel(int levelIndex) {
        int current = getUnlockedLevels();
        if (levelIndex + 1 > current) {
            prefs.putInteger(com.game.angrybirds.utils.GameConstants.KEY_UNLOCKED_LEVELS, levelIndex + 1);
            prefs.flush();
        }
    }

    public void flush() {
        prefs.flush();
    }
}
