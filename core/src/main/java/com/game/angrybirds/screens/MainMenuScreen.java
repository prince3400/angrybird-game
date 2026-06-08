package com.game.angrybirds.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.angrybirds.AngryBirdsGame;
import com.game.angrybirds.ui.UIFactory;
import com.game.angrybirds.utils.GameConstants;

/**
 * Main menu with Play, Level Select, and Quit options.
 */
public class MainMenuScreen extends AbstractScreen {

    public MainMenuScreen(AngryBirdsGame game) {
        super(game);
        buildUI();
    }

    private void buildUI() {
        stage.clear();
        Table table = UIFactory.createMenuTable();

        Label title = new Label("Angry Birds Clone", UIFactory.createDefaultSkin(game.getAssets()));
        title.setFontScale(2f);
        table.add(title).padBottom(40).row();

        table.add(UIFactory.createButton("Play", game.getAssets(), () ->
                game.getScreenManager().showGame(0))).pad(10).width(250).height(60).row();

        table.add(UIFactory.createButton("Level Select", game.getAssets(), () ->
                game.getScreenManager().showLevelSelect())).pad(10).width(250).height(60).row();

        table.add(UIFactory.createButton("Quit", game.getAssets(), Gdx.app::exit))
                .pad(10).width(250).height(60).row();

        Label highScore = new Label("High Score: " + game.getSaveManager().getHighScore(),
                UIFactory.createDefaultSkin(game.getAssets()));
        table.add(highScore).padTop(30).row();

        stage.addActor(table);
    }

    @Override
    public void show() {
        super.show();
        game.getSoundManager().playMenuMusic();
        buildUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.53f, 0.81f, 0.98f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        Sprite sky = new Sprite(game.getAssets().region(game.getAssets().skyTexture));
        sky.setSize(GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT);
        sky.draw(game.getBatch());
        game.getBatch().end();

        stage.act(delta);
        stage.draw();
    }
}
