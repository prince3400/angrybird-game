package com.game.angrybirds.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.angrybirds.AngryBirdsGame;
import com.game.angrybirds.ui.UIFactory;

/**
 * End-of-level screen showing win/lose state and score.
 */
public class GameOverScreen extends AbstractScreen {

    private int levelIndex;
    private int score;
    private boolean won;

    public GameOverScreen(AngryBirdsGame game) {
        super(game);
    }

    public void init(int levelIndex, int score, boolean won) {
        this.levelIndex = levelIndex;
        this.score = score;
        this.won = won;
        buildUI();
    }

    private void buildUI() {
        stage.clear();

        // UI container. The background will be drawn in render() using a sprite.
        Table table = UIFactory.createMenuTable();




        String titleText = won ? "Victory!" : "Game Over";
        Label title = new Label(titleText, UIFactory.createDefaultSkin(game.getAssets()));
        title.setFontScale(2f);
        table.add(title).padBottom(20).row();

        Label scoreLabel = new Label("Score: " + score, UIFactory.createDefaultSkin(game.getAssets()));
        scoreLabel.setFontScale(1.3f);
        table.add(scoreLabel).padBottom(30).row();

        if (won && levelIndex + 1 < game.getLevelLoader().getLevelCount()) {
            table.add(UIFactory.createButton("Next Level", game.getAssets(), () ->
                    game.getScreenManager().showGame(levelIndex + 1)))
                    .pad(10).width(250).height(60).row();
        }

        table.add(UIFactory.createButton("Retry", game.getAssets(), () ->
                game.getScreenManager().showGame(levelIndex)))
                .pad(10).width(250).height(60).row();

        table.add(UIFactory.createButton("Main Menu", game.getAssets(), () ->
                game.getScreenManager().showMainMenu()))
                .pad(10).width(250).height(60).row();

        stage.addActor(table);

    }

    @Override
    public void show() {
        super.show();
        if (won) {
            game.getSoundManager().playWin();
            game.getSaveManager().setHighScore(score);
            game.getSaveManager().unlockLevel(levelIndex);
        } else {
            game.getSoundManager().playLose();
        }
        buildUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(won ? 0.2f : 0.4f, won ? 0.5f : 0.15f, won ? 0.3f : 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw game-over background behind UI.
        // Requirement: use backmenu.png from assets/angrybirds/ (repo currently contains backmenu.jpg).
        com.badlogic.gdx.graphics.Texture backTex = null;
        if (Gdx.files.internal("angrybirds/backmenu.png").exists()) {
            backTex = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("angrybirds/backmenu.png"));
        } else if (Gdx.files.internal("angrybirds/backmenu.jpg").exists()) {
            backTex = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("angrybirds/backmenu.jpg"));
        }

        if (backTex != null) {
            com.badlogic.gdx.graphics.g2d.Sprite bg = new com.badlogic.gdx.graphics.g2d.Sprite(
                    game.getAssets().region(backTex));
            bg.setSize(com.game.angrybirds.utils.GameConstants.WORLD_WIDTH, com.game.angrybirds.utils.GameConstants.WORLD_HEIGHT);
            bg.setPosition(0, 0);
            game.getBatch().begin();
            bg.draw(game.getBatch());
            game.getBatch().end();
            backTex.dispose();
        } else if (game.getAssets().mainMenuBackground != null) {
            // Fallback to the generic background.
            com.badlogic.gdx.graphics.g2d.Sprite bg = new com.badlogic.gdx.graphics.g2d.Sprite(
                    game.getAssets().region(game.getAssets().mainMenuBackground));
            bg.setSize(com.game.angrybirds.utils.GameConstants.WORLD_WIDTH, com.game.angrybirds.utils.GameConstants.WORLD_HEIGHT);
            bg.setPosition(0, 0);
            game.getBatch().begin();
            bg.draw(game.getBatch());
            game.getBatch().end();
        }


        stage.act(delta);
        stage.draw();
    }

}
