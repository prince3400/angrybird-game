package com.game.angrybirds.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Persists high scores and unlocked level progress using LibGDX Preferences.
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Persists high scores, unlocked level progress, and game state using LibGDX Preferences.
 */
public class SaveManager {

    private final Preferences prefs;
    private final Json json;

    public SaveManager() {
        prefs = Gdx.app.getPreferences(com.game.angrybirds.utils.GameConstants.PREFS_NAME);
        json = new Json();
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

    /**
     * Saves the entire current game state for a given level.
     * This includes entity positions, velocities, health, scores, etc.
     *
     * @param levelIndex The index of the level being saved.
     * @param gameState The object containing all relevant game state data.
     */
    public <T> void saveGameState(int levelIndex, T gameState) {
        String jsonString = json.toJson(gameState);
        prefs.putInteger("saved_level_index", levelIndex);
        prefs.putString("saved_game_state", jsonString);
        prefs.flush();
    }

    /**
     * Loads the saved game state for a specific level.
     * @param levelIndex The index of the level to load.
     * @param type The class type to deserialize the JSON into.
     * @return The deserialized game state object, or null if no save exists or loading fails.
     */
    public <T> T loadGameState(int levelIndex, Class<T> type) {
        if (getSavedLevelIndex() != levelIndex) {
            return null;
        }
        String jsonString = prefs.getString("saved_game_state", null);
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        try {
            return json.fromJson(type, jsonString);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to load game state for level " + levelIndex, e);
            return null;
        }
    }

    public int getSavedLevelIndex() {
        return prefs.getInteger("saved_level_index", 0);
    }

    public boolean hasSavedGame() {
        return prefs.contains("saved_game_state") && !prefs.getString("saved_game_state").isEmpty();
    }

    public void clearSavedGame() {
        prefs.remove("saved_level_index");
        prefs.remove("saved_game_state");
        prefs.flush();
    }

    public void flush() {
        prefs.flush();
    }
}
