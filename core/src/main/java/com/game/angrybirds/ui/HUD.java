package com.game.angrybirds.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.game.angrybirds.assets.GameAssets;
import com.game.angrybirds.managers.ScoreManager;

/**
 * In-game HUD and overlay popups (score, birds remaining, level complete).
 */
public class HUD {

    private final ScoreManager scoreManager;
    private final BitmapFont font;
    private Window levelCompleteWindow;
    private boolean showingLevelComplete;

    private Label scoreLabel;
    private Label birdsLabel;

    public HUD(ScoreManager scoreManager, GameAssets assets) {
        this.scoreManager = scoreManager;
        this.font = assets.font;
        // Initialize labels (you might want to create a stage or a table to hold them properly)
        // For now, we'll just update their text in render.
        Skin skin = UIFactory.createDefaultSkin(assets); // Assuming UIFactory and Skin are accessible
        scoreLabel = new Label("Score: " + scoreManager.getScore(), skin);
        birdsLabel = new Label("Birds: " + scoreManager.getBirdsRemaining(), skin);
        scoreLabel.setPosition(20, com.game.angrybirds.utils.GameConstants.WORLD_HEIGHT - 20);
        birdsLabel.setPosition(20, com.game.angrybirds.utils.GameConstants.WORLD_HEIGHT - 50);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateBirdsRemaining(int birdsRemaining) {
        birdsLabel.setText("Birds: " + birdsRemaining);
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, scoreLabel.getText().toString(), scoreLabel.getX(), scoreLabel.getY());
        font.draw(batch, birdsLabel.getText().toString(), birdsLabel.getX(), birdsLabel.getY());
        batch.end();
    }

    public Table createLevelCompletePopup(GameAssets assets, int score, Runnable onContinue) {
        Skin skin = UIFactory.createDefaultSkin(assets);
        levelCompleteWindow = new Window("Level Complete!", skin);
        levelCompleteWindow.setModal(true);
        levelCompleteWindow.pad(20);

        Label scoreLabel = new Label("Score: " + score, skin);
        levelCompleteWindow.add(scoreLabel).pad(10).row();
        levelCompleteWindow.add(UIFactory.createButton("Continue", assets, onContinue)).pad(10);

        levelCompleteWindow.pack();
        levelCompleteWindow.setPosition(
                (com.game.angrybirds.utils.GameConstants.WORLD_WIDTH - levelCompleteWindow.getWidth()) / 2f,
                (com.game.angrybirds.utils.GameConstants.WORLD_HEIGHT - levelCompleteWindow.getHeight()) / 2f
        );
        showingLevelComplete = true;
        return levelCompleteWindow;
    }

    public boolean isShowingLevelComplete() {
        return showingLevelComplete;
    }

    public void hideLevelComplete() {
        showingLevelComplete = false;
        levelCompleteWindow = null;
    }
}
