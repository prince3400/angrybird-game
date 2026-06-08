package com.game.angrybirds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.game.angrybirds.assets.GameAssets;
import com.game.angrybirds.levels.LevelLoader;
import com.game.angrybirds.managers.SaveManager;
import com.game.angrybirds.managers.ScreenManager;
import com.game.angrybirds.managers.SoundManager;
import com.game.angrybirds.utils.GameConstants;

/**
 * Root application class. Owns shared managers and delegates to ScreenManager.
 */
public class AngryBirdsGame extends Game {

    private SpriteBatch batch;
    private GameAssets assets;
    private ScreenManager screenManager;
    private SoundManager soundManager;
    private SaveManager saveManager;
    private LevelLoader levelLoader;

    @Override
    public void create() {
        batch = new SpriteBatch();

        assets = new GameAssets();
        assets.load();

        saveManager = new SaveManager();
        soundManager = new SoundManager(assets);
        levelLoader = new LevelLoader();
        screenManager = new ScreenManager(this);

        screenManager.showMainMenu();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public GameAssets getAssets() {
        return assets;
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public LevelLoader getLevelLoader() {
        return levelLoader;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (screenManager != null) screenManager.dispose();
        if (batch != null) batch.dispose();
        if (assets != null) assets.dispose();
        if (soundManager != null) soundManager.dispose();
    }
}
