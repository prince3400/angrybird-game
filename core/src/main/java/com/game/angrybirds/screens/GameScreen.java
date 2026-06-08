package com.game.angrybirds.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.game.angrybirds.AngryBirdsGame;
import com.game.angrybirds.entities.Slingshot;
import com.game.angrybirds.entities.birds.Bird;
import com.game.angrybirds.entities.blocks.Block;
import com.game.angrybirds.entities.pigs.Pig;
import com.game.angrybirds.managers.LevelManager;
import com.game.angrybirds.managers.ScoreManager;
import com.game.angrybirds.physics.GameContactListener;
import com.game.angrybirds.physics.PhysicsWorldManager;
import com.game.angrybirds.ui.HUD;
import com.game.angrybirds.utils.GameConstants;

/**
 * Main gameplay screen: physics simulation, slingshot input, win/lose logic.
 */
public class GameScreen extends AbstractScreen {

    private enum GameState {
        PLAYING,
        WAITING_FOR_BIRD,
        LEVEL_COMPLETE,
        GAME_OVER,
        PAUSED
    }

    private PhysicsWorldManager physicsManager;
    private LevelManager levelManager;
    private ScoreManager scoreManager;
    private GameContactListener contactListener;
    private HUD hud;

    private Sprite trajectoryDot;
    private int levelIndex;
    private GameState gameState = GameState.PLAYING;
    private float waitTimer;
    private boolean paused;

    private final Vector3 touchPos = new Vector3();
    private InputAdapter gameInput;

    public GameScreen(AngryBirdsGame game) {
        super(game);
    }

    public void initLevel(int levelIndex) {
        this.levelIndex = levelIndex;
        disposeLevel();

        physicsManager = new PhysicsWorldManager();
        levelManager = new LevelManager(game.getLevelLoader());
        scoreManager = new ScoreManager();
        hud = new HUD(scoreManager, game.getAssets());
        trajectoryDot = new Sprite(game.getAssets().region(game.getAssets().trajectoryDot));

        contactListener = new GameContactListener(scoreManager, game.getSoundManager());
        contactListener.setCallback(new GameContactListener.CollisionCallback() {
            @Override
            public void onPigDestroyed(Pig pig) {
                checkWinCondition();
            }

            @Override
            public void onBlockDestroyed(Block block) {
                // Score handled in contact listener
            }

            @Override
            public void onBirdHit() {
                // Optional feedback
            }
        });
        physicsManager.setContactListener(contactListener);

        levelManager.loadLevel(levelIndex, physicsManager.getWorld(), game.getAssets());
        scoreManager.reset(levelManager.getTotalBirds());
        gameState = GameState.PLAYING;
        waitTimer = 0f;
        paused = false;

        setupInput();
        game.getSoundManager().playGameMusic();
    }

    private void setupInput() {
        gameInput = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (gameState == GameState.PLAYING) {
                    Slingshot slingshot = levelManager.getSlingshot();
                    if (slingshot != null && slingshot.getAttachedBird() != null) {
                        touchPos.set(screenX, screenY, 0);
                        camera.unproject(touchPos);
                        slingshot.startDrag(touchPos.x, touchPos.y);
                        return true;
                    }
                } else if (gameState == GameState.WAITING_FOR_BIRD) {
                    activateBirdAbility();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (gameState != GameState.PLAYING) return false;
                Slingshot slingshot = levelManager.getSlingshot();
                if (slingshot == null || !slingshot.isDragging()) return false;

                touchPos.set(screenX, screenY, 0);
                camera.unproject(touchPos);
                slingshot.updateDrag(touchPos.x, touchPos.y);
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (gameState != GameState.PLAYING) return false;
                Slingshot slingshot = levelManager.getSlingshot();
                if (slingshot == null || !slingshot.isDragging()) return false;

                Vector2 launchVel = slingshot.release();
                if (launchVel != null) {
                    game.getSoundManager().playLaunch();
                    scoreManager.birdUsed();
                }
                gameState = GameState.WAITING_FOR_BIRD;
                waitTimer = 0f;
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.P) {
                    pauseGame();
                    return true;
                }
                if (keycode == Input.Keys.SPACE) {
                    activateBirdAbility();
                    return true;
                }
                return false;
            }
        };

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(gameInput);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void activateBirdAbility() {
        for (Bird bird : levelManager.getActiveBirds()) {
            if (bird.isLaunched() && !bird.isSettled()) {
                bird.activateAbility();
                break;
            }
        }
    }

    public void pauseGame() {
        if (gameState == GameState.PAUSED) return;
        paused = true;
        gameState = GameState.PAUSED;
        game.getScreenManager().showPause(this);
    }

    public void resumeFromPause() {
        paused = false;
        gameState = GameState.PLAYING;
        setupInput();
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    private void checkWinCondition() {
        if (levelManager.allPigsDestroyed()) {
            scoreManager.addRemainingBirdBonus();
            gameState = GameState.LEVEL_COMPLETE;
            game.getSoundManager().playWin();
            waitTimer = 0f;
        }
    }

    private void checkLoseCondition() {
        if (levelManager.allBirdsUsed() && levelManager.isCurrentBirdSettled()) {
            if (!levelManager.allPigsDestroyed()) {
                gameState = GameState.GAME_OVER;
                game.getSoundManager().playLose();
                waitTimer = 1.5f;
            }
        }
    }

    @Override
    public void show() {
        setupInput();
    }

    @Override
    public void render(float delta) {
        if (!paused) {
            update(delta);
        }
        draw(delta);
    }

    public void renderPaused(float delta) {
        draw(delta);
    }

    private void update(float delta) {
        switch (gameState) {
            case PLAYING:
                physicsManager.update(delta);
                updateEntities(delta);
                levelManager.removeDeadEntities(physicsManager.getWorld());
                break;

            case WAITING_FOR_BIRD:
                physicsManager.update(delta);
                updateEntities(delta);
                levelManager.removeDeadEntities(physicsManager.getWorld());
                waitTimer += delta;

                if (levelManager.isCurrentBirdSettled()) {
                    if (levelManager.allPigsDestroyed()) {
                        scoreManager.addRemainingBirdBonus();
                        gameState = GameState.LEVEL_COMPLETE;
                        waitTimer = 0f;
                    } else if (levelManager.allBirdsUsed()) {
                        gameState = GameState.GAME_OVER;
                        waitTimer = 1.5f;
                    } else {
                        levelManager.spawnNextBird();
                        gameState = GameState.PLAYING;
                    }
                }
                checkLoseCondition();
                break;

            case LEVEL_COMPLETE:
                waitTimer += delta;
                if (waitTimer > 2f) {
                    game.getScreenManager().showGameOver(levelIndex, scoreManager.getScore(), true);
                }
                break;

            case GAME_OVER:
                waitTimer -= delta;
                if (waitTimer <= 0f) {
                    game.getScreenManager().showGameOver(levelIndex, scoreManager.getScore(), false);
                }
                break;

            default:
                break;
        }
    }

    private void updateEntities(float delta) {
        for (Bird bird : levelManager.getActiveBirds()) bird.update(delta);
        for (Block block : levelManager.getBlocks()) block.update(delta);
        for (Pig pig : levelManager.getPigs()) pig.update(delta);
    }

    private void draw(float delta) {
        Gdx.gl.glClearColor(0.53f, 0.81f, 0.98f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();
        // Ground strip
        Sprite ground = new Sprite(game.getAssets().region(game.getAssets().groundTexture));
        ground.setSize(GameConstants.WORLD_WIDTH, 40);
        ground.setPosition(0, 40);
        ground.draw(game.getBatch());

        Slingshot slingshot = levelManager.getSlingshot();
        if (slingshot != null) {
            slingshot.renderTrajectory(game.getBatch(), trajectoryDot);
            slingshot.render(game.getBatch());
        }

        for (Block block : levelManager.getBlocks()) block.render(game.getBatch());
        for (Pig pig : levelManager.getPigs()) pig.render(game.getBatch());
        for (Bird bird : levelManager.getActiveBirds()) bird.render(game.getBatch());

        game.getBatch().end();

        hud.render(game.getBatch());

        stage.act(delta);
        stage.draw();
    }

    private void disposeLevel() {
        if (levelManager != null && physicsManager != null) {
            levelManager.clear(physicsManager.getWorld());
        }
        if (physicsManager != null) {
            physicsManager.dispose();
            physicsManager = null;
        }
    }

    @Override
    public void dispose() {
        disposeLevel();
        super.dispose();
    }
}
