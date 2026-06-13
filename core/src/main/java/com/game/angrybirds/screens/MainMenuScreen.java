package com.game.angrybirds.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Batch;

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

        // Clear and re-create menu UI every time to prevent any leftover/overlay UI artifacts.
        // Big, centered title (top middle).
        Label title = new Label("ANGRY BIRDS", UIFactory.createDefaultSkin(game.getAssets()));
        title.setFontScale(5.0f);
        title.setColor(Color.WHITE);
        table.add(title).padBottom(10).row();

        // (Do not add any other debug/gibberish labels.)

        // Buttons


        table.add(UIFactory.createButton("Play", game.getAssets(), () ->
                game.getScreenManager().showGame(0))).pad(10).width(250).height(60).row();

        table.add(UIFactory.createButton("Level Select", game.getAssets(), () ->
                game.getScreenManager().showLevelSelect())).pad(10).width(250).height(60).row();






        if (game.getSaveManager().hasSavedGame()) {
            table.add(UIFactory.createButton("LOAD", game.getAssets(), () -> {
                int savedLevel = game.getSaveManager().getSavedLevelIndex();
                game.getScreenManager().showGame(savedLevel);
            })).pad(10).width(250).height(60).row();
        }

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

        // Base background.
Sprite sky = new Sprite(game.getAssets().region(game.getAssets().mainMenuBackground));
        sky.setSize(GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT);
        sky.draw(game.getBatch());

        // Decorative layer (adds “angry birds” vibe using existing repo textures).
        // Note: We intentionally draw only sprites here (UI is handled by stage.draw()).
        Sprite redbird = new Sprite(game.getAssets().region(game.getAssets().birdRed));
        Sprite pig = new Sprite(game.getAssets().region(game.getAssets().pigTexture));

        // Scale + position for background composition.
        redbird.setSize(140, 140);
        pig.setSize(120, 120);

        // Parallax-ish placement: slight vertical offsets.
        redbird.setPosition(GameConstants.WORLD_WIDTH * 0.18f, GameConstants.WORLD_HEIGHT * 0.55f);
        pig.setPosition(GameConstants.WORLD_WIDTH * 0.70f, GameConstants.WORLD_HEIGHT * 0.42f);

        // Soft shadow/contrast overlay to keep menu readable.
        // (Draw a semi-transparent rectangle by tinting one of the sprites.)
        // We reuse the sky region sprite as a tinted fullscreen overlay.
        Color prev = game.getBatch().getColor();
        game.getBatch().setColor(0f, 0f, 0f, 0.25f);
        sky.draw(game.getBatch());
        game.getBatch().setColor(prev);

        redbird.draw(game.getBatch());
        pig.draw(game.getBatch());

        game.getBatch().end();


        stage.act(delta);
        stage.draw();

    }
}

