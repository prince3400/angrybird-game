package com.game.angrybirds.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.angrybirds.AngryBirdsGame;
import com.game.angrybirds.ui.UIFactory;

/**
 * Pause overlay with resume, restart, and main menu options.
 */
public class PauseScreen extends AbstractScreen {

    private GameScreen gameScreen;

    public PauseScreen(AngryBirdsGame game) {
        super(game);
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        super.show();
        buildUI();
    }

    private void buildUI() {
        stage.clear();
        Table table = UIFactory.createMenuTable();

        Label title = new Label("Paused", UIFactory.createDefaultSkin(game.getAssets()));
        title.setFontScale(2f);
        table.add(title).padBottom(30).row();

        table.add(UIFactory.createButton("RESUME", game.getAssets(), () -> {

            if (gameScreen != null) {
                game.setScreen(gameScreen);
                gameScreen.resumeFromPause();
            }
        })).pad(10).width(250).height(60).row();

        table.add(UIFactory.createButton("Restart", game.getAssets(), () -> {
            if (gameScreen != null) {
                int level = gameScreen.getLevelIndex();
                game.getScreenManager().showGame(level);
            }
        })).pad(10).width(250).height(60).row();

        table.add(UIFactory.createButton("QUIT", game.getAssets(), () ->
                game.getScreenManager().showMainMenu())).pad(10).width(250).height(60).row();


        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // Draw paused game underneath
        if (gameScreen != null) {
            gameScreen.renderPaused(delta);
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        // Dim overlay
        Gdx.gl.glClearColor(0, 0, 0, 0.5f);

        stage.act(delta);
        stage.draw();
    }
}
