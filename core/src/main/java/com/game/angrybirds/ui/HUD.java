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

    public HUD(ScoreManager scoreManager, GameAssets assets) {
        this.scoreManager = scoreManager;
        this.font = assets.font;
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Score: " + scoreManager.getScore(), 20, com.game.angrybirds.utils.GameConstants.WORLD_HEIGHT - 20);
        font.draw(batch, "Birds: " + scoreManager.getBirdsRemaining(), 20, com.game.angrybirds.utils.GameConstants.WORLD_HEIGHT - 50);
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
