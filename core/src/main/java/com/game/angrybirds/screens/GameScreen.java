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
import com.game.angrybirds.levels.BlockType;
import com.game.angrybirds.levels.BirdType;
import com.game.angrybirds.managers.LevelManager;
import com.game.angrybirds.managers.ScoreManager;
import com.game.angrybirds.physics.BodyFactory;
import com.game.angrybirds.physics.GameContactListener;
import com.game.angrybirds.physics.PhysicsWorldManager;
import com.game.angrybirds.ui.HUD;
import com.game.angrybirds.utils.GameConstants;
import com.game.angrybirds.screens.GameStateSave;
import com.game.angrybirds.screens.GameStateSave.BlockSave;
import com.game.angrybirds.screens.GameStateSave.PigSave;
import com.game.angrybirds.screens.GameStateSave.BirdSave;
import com.game.angrybirds.levels.LevelData;

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

    private boolean pauseButtonInitialized = false;


    public GameScreen(AngryBirdsGame game) {
        super(game);
    }

    public void initLevel(int levelIndex) {
        this.levelIndex = levelIndex;
        disposeLevel();
        if (scoreManager == null) {
            scoreManager = new ScoreManager();
        }


        // If we are loading a game, the managers are already initialized.
        // Otherwise, initialize for a new game.
        boolean isLoadingSave = game.getSaveManager().hasSavedGame()
                && game.getSaveManager().getSavedLevelIndex() == levelIndex;

        // Ensure managers are initialized before attempting to restore a saved game
        if (physicsManager == null) {
            physicsManager = new PhysicsWorldManager();
        }
        if (levelManager == null) {
            levelManager = new LevelManager(game.getLevelLoader());
        }
        if (scoreManager == null) {
            scoreManager = new ScoreManager();
        }
        if (hud == null) {
            hud = new HUD(scoreManager, game.getAssets());
        }
        if (trajectoryDot == null) {
            trajectoryDot = new Sprite(game.getAssets().region(game.getAssets().trajectoryDot));
        }

        if (contactListener == null) {
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
        }

        if (isLoadingSave) {
            // If loading, managers are already initialized, just restore state
            maybeRestoreSavedGame();
        } else {
            // New game: load level from scratch.
            levelManager.loadLevel(levelIndex, physicsManager.getWorld(), game.getAssets());
            scoreManager.reset(levelManager.getTotalBirds());
            gameState = GameState.PLAYING;
            waitTimer = 0f;
            paused = false;
            setupInput();
            game.getSoundManager().playGameMusic();
        }

        // After init (new or loaded), ensure HUD reflects current scoreManager
        hud.updateScore(scoreManager.getScore());
        hud.updateBirdsRemaining(scoreManager.getBirdsRemaining());

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
        initPauseButtonIfNeeded();
    }

    private void initPauseButtonIfNeeded() {
        if (pauseButtonInitialized) return;
        pauseButtonInitialized = true;

        // Top-right pause button (touch/click).
        // Position/size are in world coordinates used by the screen's FitViewport.
        float buttonWidth = 110f;
        float buttonHeight = 50f;
        float x = GameConstants.WORLD_WIDTH - buttonWidth - 20f;
        float pauseY = GameConstants.WORLD_HEIGHT - buttonHeight - 20f;
        float gapY = 10f;
        float saveY = pauseY - buttonHeight - gapY;

        // Ensure this UI button is clickable; stage is also used for touch input via multiplexer.
        com.badlogic.gdx.scenes.scene2d.ui.TextButton pauseButton =
                com.game.angrybirds.ui.UIFactory.createButton("PAUSE", game.getAssets(), this::pauseGame);
        pauseButton.setBounds(x, pauseY, buttonWidth, buttonHeight);
        stage.addActor(pauseButton);

        com.badlogic.gdx.scenes.scene2d.ui.TextButton saveButton =
                com.game.angrybirds.ui.UIFactory.createButton("SAVE", game.getAssets(), this::saveGame);
        saveButton.setBounds(x, saveY, buttonWidth, buttonHeight);
        stage.addActor(saveButton);
        saveButton.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
        pauseButton.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

    }

    private float saveFlashTimer = 0f;
    private com.badlogic.gdx.scenes.scene2d.ui.Label saveFlashLabel;

    private void showSaveFlash() {
        saveFlashTimer = 0.35f;
        if (saveFlashLabel == null) {
            saveFlashLabel = new com.badlogic.gdx.scenes.scene2d.ui.Label("SAVE", com.game.angrybirds.ui.UIFactory.createDefaultSkin(game.getAssets()));
            saveFlashLabel.setFontScale(4.5f);
        }
        saveFlashLabel.setColor(1f, 1f, 1f, 1f);
        saveFlashLabel.setVisible(true);
        if (saveFlashLabel.getParent() == null) {
            saveFlashLabel.setPosition(GameConstants.WORLD_WIDTH / 2f - saveFlashLabel.getWidth() / 2f,
                    GameConstants.WORLD_HEIGHT * 0.72f);
            stage.addActor(saveFlashLabel);
        }
        saveFlashLabel.toFront();
    }

    private void saveGame() {
        showSaveFlash();
        // Serialize current level state (entities + physics) into JSON.
        // Deterministic restore relies on LevelManager spawning entities in the same order for a level.
        try {
            GameStateSave gameStateSave = new GameStateSave();

            // ScoreManager state
            gameStateSave.currentScore = scoreManager.getScore();
            gameStateSave.birdsRemaining = scoreManager.getBirdsRemaining();

            // Blocks
            for (Block b : levelManager.getBlocks()) {
                if (b == null) continue;
                BlockSave bs = new BlockSave();
                bs.alive = b.isAlive();
                bs.type = b.getBlockType().name();
                bs.durability = b.getDurabilityPercent();
                if (b.getBody() != null) {
                    bs.x = b.getBody().getPosition().x;
                    bs.y = b.getBody().getPosition().y;
                    bs.angle = b.getBody().getAngle();
                    bs.vx = b.getBody().getLinearVelocity().x;
                    bs.vy = b.getBody().getLinearVelocity().y;
                    bs.av = b.getBody().getAngularVelocity();
                }
                gameStateSave.blocks.add(bs);
            }

            // Pigs
            for (Pig p : levelManager.getPigs()) {
                if (p == null) continue;
                PigSave ps = new PigSave();
                ps.alive = p.isAlive();
                ps.dying = p.dying;
                if (p.getBody() != null) {
                    ps.x = p.getBody().getPosition().x;
                    ps.y = p.getBody().getPosition().y;
                    ps.angle = p.getBody().getAngle();
                    ps.vx = p.getBody().getLinearVelocity().x;
                    ps.vy = p.getBody().getLinearVelocity().y;
                    ps.av = p.getBody().getAngularVelocity();
                }
                gameStateSave.pigs.add(ps);
            }

            // Birds
            for (Bird b : levelManager.getActiveBirds()) {
                if (b == null) continue;
                BirdSave bs = new BirdSave();
                bs.alive = b.isAlive();
                bs.settled = b.isSettled();
                bs.launchState = b.launchState.name();
                bs.abilityUsed = b.abilityUsed;
                bs.health = b.health;
                if (b.getBody() != null) {
                    bs.x = b.getBody().getPosition().x;
                    bs.y = b.getBody().getPosition().y;
                    bs.angle = b.getBody().getAngle();
                    bs.vx = b.getBody().getLinearVelocity().x;
                    bs.vy = b.getBody().getLinearVelocity().y;
                    bs.av = b.getBody().getAngularVelocity();
                }
                gameStateSave.birds.add(bs);
            }

            gameStateSave.currentBirdIndex = levelManager.getCurrentBirdIndex();
            gameStateSave.hasAttachedBird = levelManager.getSlingshot() != null && levelManager.getSlingshot().getAttachedBird() != null;
            gameStateSave.gameState = gameState.name();

            game.getSaveManager().saveGameState(getLevelIndex(), gameStateSave);
        } catch (Exception ignored) {
            // Keep previous save if anything goes wrong.
            Gdx.app.error("GameScreen", "Failed to save game state", ignored);
        }
    }


    private void loadGame() {
        int savedLevel = game.getSaveManager().getSavedLevelIndex();
        if (game.getSaveManager().hasSavedGame()) {
            game.getScreenManager().showGame(savedLevel);
        }
    }


    private void maybeRestoreSavedGame() {
            GameStateSave restoredState = game.getSaveManager().loadGameState(getLevelIndex(), GameStateSave.class);
        if (restoredState == null) {
            Gdx.app.log("GameScreen", "No saved game state found for level " + getLevelIndex());
            // If no saved state, load the level from its initial definition
            levelManager.loadLevel(levelIndex, physicsManager.getWorld(), game.getAssets());
            scoreManager.reset(levelManager.getTotalBirds());
            gameState = GameState.PLAYING;
            waitTimer = 0f;
            paused = false;
            setupInput();
            game.getSoundManager().playGameMusic();
            return;
        }

        Gdx.app.log("SaveRestore", "Restoring game state for levelIndex=" + getLevelIndex());

        try {
            // Restore ScoreManager state
            scoreManager.setScore(restoredState.currentScore);
            scoreManager.setBirdsRemaining(restoredState.birdsRemaining);

            // Clear existing entities before restoring
            levelManager.clear(physicsManager.getWorld());

            // Re-initialize ground and slingshot (these are always the same per level)
            BodyFactory.createGround(physicsManager.getWorld(), GameConstants.WORLD_WIDTH / 2f, 60f,
                    GameConstants.WORLD_WIDTH, 40f);
            // Slingshot (combine slingshot.png + slingpart.png at runtime)
            Sprite slingshotSprite = new Sprite(game.getAssets().region(game.getAssets().slingshotTexture));
            Sprite slingPartSprite = null;
            try {
                slingPartSprite = new Sprite(game.getAssets().region(new com.badlogic.gdx.graphics.Texture(
                        com.badlogic.gdx.Gdx.files.internal("angrybirds/slingpart.png"))));
            } catch (Exception ignored) {
                slingPartSprite = null;
            }
            if (slingPartSprite != null) {
                levelManager.setSlingshot(new Slingshot(slingshotSprite, slingPartSprite, 0, 0));
            } else {
                levelManager.setSlingshot(new Slingshot(slingshotSprite));
            }

            // Blocks
            for (int i = 0; i < restoredState.blocks.size; i++) {
                BlockSave bs = restoredState.blocks.get(i);
                Block block = com.game.angrybirds.entities.blocks.BlockFactory.create(physicsManager.getWorld(), game.getAssets(), 
                        BlockType.valueOf(bs.type), bs.x, bs.y);
                block.durability = block.maxDurability * bs.durability; // Restore durability
                levelManager.getBlocks().add(block);
                applyEntityState(block, bs.alive, bs.x, bs.y, bs.angle, bs.vx, bs.vy, bs.av);
            }

            // Pigs
            for (int i = 0; i < restoredState.pigs.size; i++) {
                PigSave ps = restoredState.pigs.get(i);
                Sprite pigSprite = new Sprite(game.getAssets().region(game.getAssets().pigTexture));
                float size = GameConstants.PIG_RADIUS * 2f;
                pigSprite.setSize(size, size);
                Pig pig = new Pig(physicsManager.getWorld(), pigSprite, ps.x, ps.y);
                if (!ps.alive && ps.dying) {
                    pig.startDeath(); // Re-trigger death animation if needed
                }
                levelManager.getPigs().add(pig);
                applyEntityState(pig, ps.alive, ps.x, ps.y, ps.angle, ps.vx, ps.vy, ps.av);
            }

            // Birds: Rebuild queue based on original level data, then adjust for saved state
            levelManager.getBirdQueue().clear(); // Clear existing bird queue
            levelManager.getActiveBirds().clear(); // Clear existing active birds

            // Rebuild the birdQueue based on the original level definition
            LevelData originalLevelData = game.getLevelLoader().getLevel(levelIndex);
            for (BirdType birdType : originalLevelData.birds) {
                Bird bird = com.game.angrybirds.entities.birds.BirdFactory.create(physicsManager.getWorld(), game.getAssets(), birdType,
                        GameConstants.SLINGSHOT_X, GameConstants.SLINGSHOT_Y + 100);
                levelManager.getBirdQueue().add(bird);
            }

            levelManager.setCurrentBirdIndex(0);
            levelManager.getActiveBirds().clear();
            if (levelManager.getSlingshot() != null) {
                levelManager.getSlingshot().attachBird(null);
            }

            for (int spawn = 0; spawn < restoredState.currentBirdIndex; spawn++) {
                levelManager.spawnNextBird();
            }

            // Apply state to active birds (birds already spawned and in the world)
            for (int i = 0; i < restoredState.birds.size; i++) {
                BirdSave bs = restoredState.birds.get(i);
                Bird bird = levelManager.getActiveBirds().get(i); // Get already spawned bird

                applyEntityState(bird, bs.alive, bs.x, bs.y, bs.angle, bs.vx, bs.vy, bs.av);

                if (bs.alive) {
                    bird.abilityUsed = bs.abilityUsed;
                    bird.health = bs.health;
                    bird.setLaunchState(Bird.LaunchState.valueOf(bs.launchState));

                    boolean shouldBeAttached = restoredState.hasAttachedBird && (i == restoredState.currentBirdIndex - 1);

                    if (bs.settled) {
                        bird.setLaunchState(Bird.LaunchState.SETTLED);
                    } else if (shouldBeAttached) {
                        bird.setLaunchState(Bird.LaunchState.ON_SLINGSHOT);
                        levelManager.getSlingshot().attachBird(bird); // Re-attach the bird
                    } else {
                        bird.setLaunchState(Bird.LaunchState.LAUNCHED);
                    }
                }
            }

            // Restore game state
            gameState = GameState.valueOf(restoredState.gameState);
            waitTimer = 0f; // Reset waitTimer on load
            paused = false;
            setupInput();
            game.getSoundManager().playGameMusic();

            Gdx.app.log("SaveRestore", "Game state restored successfully.");
            game.getSaveManager().clearSavedGame(); // Clear save after successful load

        } catch (Exception e) {
            Gdx.app.error("SaveRestore", "Failed to restore game state", e);
            // Fallback to fresh level load if restore fails
            levelManager.loadLevel(levelIndex, physicsManager.getWorld(), game.getAssets());
            scoreManager.reset(levelManager.getTotalBirds());
            gameState = GameState.PLAYING;
            waitTimer = 0f;
            paused = false;
            setupInput();
            game.getSoundManager().playGameMusic();
        }
    }


    private void applyEntityState(com.game.angrybirds.entities.Entity entity,
                                    boolean alive,
                                    Float x, Float y,
                                    Float angle,
                                    Float vx, Float vy,
                                    Float av) {
        if (entity == null) return;

        if (!alive) {
            entity.markForRemoval();
            if (entity.getBody() != null) {
                entity.getBody().setAwake(false);
                entity.getBody().setLinearVelocity(0, 0);
                entity.getBody().setAngularVelocity(0);
            }
            return;
        }

        if (entity.getBody() == null) return;
        com.badlogic.gdx.physics.box2d.Body body = entity.getBody();

        if (x != null && y != null) {
            body.setTransform(x, y, angle == null ? body.getAngle() : angle);
        }
        if (vx != null && vy != null) {
            body.setLinearVelocity(vx, vy);
        }
        if (av != null) {
            body.setAngularVelocity(av);
        }

        body.setAwake(true);
    }

    // Helper method to safely extract float from JsonValue, handling potential type mismatches
    private Float getFloatFromJson(com.badlogic.gdx.utils.JsonValue jsonValue, String key) {
        com.badlogic.gdx.utils.JsonValue value = jsonValue.get(key);
        if (value != null && value.isNumber()) {
            return value.asFloat();
        } else if (value != null && value.isString()) {
            try {
                return Float.parseFloat(value.asString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }




    @Override
    public void render(float delta) {
        if (!paused) {
            update(delta);
        }
        // Animate SAVE flash overlay.
        if (saveFlashTimer > 0f) {
            saveFlashTimer -= delta;
            if (saveFlashTimer <= 0f && saveFlashLabel != null) {
                saveFlashLabel.setVisible(false);
            }
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
