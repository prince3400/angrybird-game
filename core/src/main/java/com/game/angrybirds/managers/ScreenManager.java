package com.game.angrybirds.managers;

import com.badlogic.gdx.utils.Array;
import com.game.angrybirds.AngryBirdsGame;
import com.game.angrybirds.screens.AbstractScreen;
import com.game.angrybirds.screens.GameOverScreen;
import com.game.angrybirds.screens.GameScreen;
import com.game.angrybirds.screens.LevelSelectScreen;
import com.game.angrybirds.screens.MainMenuScreen;
import com.game.angrybirds.screens.PauseScreen;

/**
 * Central screen factory and transition manager.
 */
public class ScreenManager {

    private final AngryBirdsGame game;
    private final Array<AbstractScreen> screenPool = new Array<>();

    public ScreenManager(AngryBirdsGame game) {
        this.game = game;
    }

    public void showMainMenu() {
        game.setScreen(getOrCreate(MainMenuScreen.class));
    }

    public void showLevelSelect() {
        game.setScreen(getOrCreate(LevelSelectScreen.class));
    }

    public void showGame(int levelIndex) {
        GameScreen screen = getOrCreate(GameScreen.class);
        screen.initLevel(levelIndex);
        game.setScreen(screen);
    }

    public void showPause(GameScreen gameScreen) {
        PauseScreen pause = getOrCreate(PauseScreen.class);
        pause.setGameScreen(gameScreen);
        game.setScreen(pause);
    }

    public void showGameOver(int levelIndex, int score, boolean won) {
        GameOverScreen screen = getOrCreate(GameOverScreen.class);
        screen.init(levelIndex, score, won);
        game.setScreen(screen);
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractScreen> T getOrCreate(Class<T> type) {
        for (AbstractScreen screen : screenPool) {
            if (type.isInstance(screen)) {
                return (T) screen;
            }
        }
        T screen = createScreen(type);
        screenPool.add(screen);
        return screen;
    }

    private <T extends AbstractScreen> T createScreen(Class<T> type) {
        if (type == MainMenuScreen.class) return type.cast(new MainMenuScreen(game));
        if (type == LevelSelectScreen.class) return type.cast(new LevelSelectScreen(game));
        if (type == GameScreen.class) return type.cast(new GameScreen(game));
        if (type == PauseScreen.class) return type.cast(new PauseScreen(game));
        if (type == GameOverScreen.class) return type.cast(new GameOverScreen(game));
        throw new IllegalArgumentException("Unknown screen type: " + type);
    }

    public void dispose() {
        for (AbstractScreen screen : screenPool) {
            screen.dispose();
        }
        screenPool.clear();
    }
}
