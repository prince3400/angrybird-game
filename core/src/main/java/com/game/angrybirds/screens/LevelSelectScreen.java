package com.game.angrybirds.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.angrybirds.AngryBirdsGame;
import com.game.angrybirds.levels.LevelData;
import com.game.angrybirds.ui.UIFactory;

/**
 * Grid of selectable levels with unlock state from save data.
 */
public class LevelSelectScreen extends AbstractScreen {

    public LevelSelectScreen(AngryBirdsGame game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        buildUI();
    }

    private void buildUI() {
        stage.clear();
        Table table = UIFactory.createMenuTable();

        Label title = new Label("Select Level", UIFactory.createDefaultSkin(game.getAssets()));
        title.setFontScale(1.5f);
        table.add(title).padBottom(30).colspan(3).row();

        int unlocked = game.getSaveManager().getUnlockedLevels();
        int col = 0;
        for (LevelData level : game.getLevelLoader().getAllLevels()) {
            int levelIndex = level.index;
            boolean isUnlocked = (levelIndex + 1) <= unlocked;
            String label = isUnlocked ? "Level " + (levelIndex + 1) + ": " + level.name : "Locked";

            table.add(UIFactory.createButton(label, game.getAssets(), () -> {
                if (levelIndex < game.getSaveManager().getUnlockedLevels()) {
                    game.getScreenManager().showGame(levelIndex);
                }
            })).pad(8).width(280).height(50);

            col++;
            if (col % 2 == 0) table.row();
        }
        if (col % 2 != 0) table.row();
        table.add(UIFactory.createButton("Back", game.getAssets(), () ->
                game.getScreenManager().showMainMenu())).colspan(3).padTop(20).width(200).height(50);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.5f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
}
