package com.game.angrybirds.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.angrybirds.AngryBirdsGame;
import com.game.angrybirds.utils.GameConstants;

/**
 * Base screen with shared camera, viewport, and Stage setup.
 */
public abstract class AbstractScreen implements Screen {

    protected final AngryBirdsGame game;
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected Stage stage;

    protected AbstractScreen(AngryBirdsGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.getBatch());
        camera.position.set(GameConstants.WORLD_WIDTH / 2f, GameConstants.WORLD_HEIGHT / 2f, 0);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
