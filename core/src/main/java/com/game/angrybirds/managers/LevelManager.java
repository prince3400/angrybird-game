package com.game.angrybirds.managers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.game.angrybirds.assets.GameAssets;
import com.game.angrybirds.entities.Slingshot;
import com.game.angrybirds.entities.birds.Bird;
import com.game.angrybirds.entities.birds.BirdFactory;
import com.game.angrybirds.entities.blocks.Block;
import com.game.angrybirds.entities.blocks.BlockFactory;
import com.game.angrybirds.entities.pigs.Pig;
import com.game.angrybirds.levels.BirdType;
import com.game.angrybirds.levels.LevelData;
import com.game.angrybirds.levels.LevelLoader;
import com.game.angrybirds.physics.BodyFactory;
import com.game.angrybirds.utils.GameConstants;

/**
 * Spawns and tracks all entities for the active level.
 */
public class LevelManager {

    private final LevelLoader levelLoader;
    private final Array<Bird> birdQueue = new Array<>();
    private final Array<Bird> activeBirds = new Array<>();
    private final Array<Block> blocks = new Array<>();
    private final Array<Pig> pigs = new Array<>();
    private final Array<Body> bodiesToDestroy = new Array<>();

    private LevelData currentLevel;
    private int currentBirdIndex;
    private Slingshot slingshot;

    public LevelManager(LevelLoader levelLoader) {
        this.levelLoader = levelLoader;
    }

    public Array<Bird> getBirdQueue() {
        return birdQueue;
    }

    public void setSlingshot(Slingshot slingshot) {
        this.slingshot = slingshot;
    }

    public void loadLevel(int levelIndex, World world, GameAssets assets) {
        clear(world);
        currentLevel = levelLoader.getLevel(levelIndex);
        currentBirdIndex = 0;

        // Ground
        BodyFactory.createGround(world, GameConstants.WORLD_WIDTH / 2f, 60f,
                GameConstants.WORLD_WIDTH, 40f);

        // Slingshot (combine slingshot.png + slingpart.png at runtime)
        Sprite slingshotSprite = new Sprite(assets.region(assets.slingshotTexture));
        Sprite slingPartSprite = null;
        // Try load slingpart.png; if missing, fall back to slingshot.png.
        try {
            slingPartSprite = new Sprite(assets.region(new com.badlogic.gdx.graphics.Texture(
                    com.badlogic.gdx.Gdx.files.internal("angrybirds/slingpart.png"))));
        } catch (Exception ignored) {
            slingPartSprite = null;
        }

        if (slingPartSprite != null) {
            slingshot = new Slingshot(slingshotSprite, slingPartSprite, 0, 0);
        } else {
            slingshot = new Slingshot(slingshotSprite);
        }


        // Blocks
        for (LevelData.BlockPlacement placement : currentLevel.blocks) {
            Block block = BlockFactory.create(world, assets, placement.type, placement.x, placement.y);
            blocks.add(block);
        }

        // Pigs
        for (LevelData.PigPlacement placement : currentLevel.pigs) {
            Sprite pigSprite = new Sprite(assets.region(assets.pigTexture));
            // Match sprite size to Box2D physics radius (pig body is a circle).
            float size = GameConstants.PIG_RADIUS * 2f;
            pigSprite.setSize(size, size);

            Pig pig = new Pig(world, pigSprite, placement.x, placement.y);
            pigs.add(pig);
        }

        // Bird queue (spawn off-screen, attach first to slingshot)
        for (BirdType birdType : currentLevel.birds) {
            Bird bird = BirdFactory.create(world, assets, birdType,
                    GameConstants.SLINGSHOT_X, GameConstants.SLINGSHOT_Y + 100);
            birdQueue.add(bird);
        }

        spawnNextBird();
    }

    public void spawnNextBird() {
        if (currentBirdIndex >= birdQueue.size) return;
        Bird bird = birdQueue.get(currentBirdIndex);
        slingshot.attachBird(bird);
        activeBirds.add(bird);
        currentBirdIndex++;
    }

    public boolean allPigsDestroyed() {
        for (Pig pig : pigs) {
            if (pig.isAlive() || pig.isDying()) return false;
        }
        return true;
    }

    public boolean allBirdsUsed() {
        return currentBirdIndex >= birdQueue.size && slingshot.getAttachedBird() == null;
    }

    public boolean isCurrentBirdSettled() {
        if (slingshot.getAttachedBird() != null) return false;
        if (activeBirds.size == 0) return true;
        Bird last = activeBirds.peek();
        return last.isSettled() || !last.isAlive();
    }

    public int getAlivePigCount() {
        int count = 0;
        for (Pig pig : pigs) {
            if (pig.isAlive()) count++;
        }
        return count;
    }

    public int getRemainingBirds() {
        int remaining = birdQueue.size - currentBirdIndex;
        if (slingshot.getAttachedBird() != null) remaining++;
        return remaining;
    }

    public int getTotalBirds() {
        return birdQueue.size;
    }

    public void markBodyForDestruction(Body body) {
        bodiesToDestroy.add(body);
    }

    public void destroyMarkedBodies(World world) {
        for (Body body : bodiesToDestroy) {
            world.destroyBody(body);
        }
        bodiesToDestroy.clear();
    }

    public void removeDeadEntities(World world) {
        for (int i = blocks.size - 1; i >= 0; i--) {
            Block block = blocks.get(i);
            if (block.isMarkedForRemoval()) {
                markBodyForDestruction(block.getBody());
                blocks.removeIndex(i);
            }
        }
        for (int i = pigs.size - 1; i >= 0; i--) {
            Pig pig = pigs.get(i);
            if (pig.isMarkedForRemoval()) {
                markBodyForDestruction(pig.getBody());
                pigs.removeIndex(i);
            }
        }
        for (int i = activeBirds.size - 1; i >= 0; i--) {
            Bird bird = activeBirds.get(i);
            if (bird.isMarkedForRemoval()) {
                markBodyForDestruction(bird.getBody());
                activeBirds.removeIndex(i);
            }
        }
        destroyMarkedBodies(world);
    }

    public void clear(World world) {
        for (Block block : blocks) {
            if (block.getBody() != null) world.destroyBody(block.getBody());
        }
        for (Pig pig : pigs) {
            if (pig.getBody() != null) world.destroyBody(pig.getBody());
        }
        for (Bird bird : birdQueue) {
            if (bird.getBody() != null) world.destroyBody(bird.getBody());
        }
        birdQueue.clear();
        activeBirds.clear();
        blocks.clear();
        pigs.clear();
        bodiesToDestroy.clear();
        slingshot = null;
        currentLevel = null; // Clear current level data
    }

    public Slingshot getSlingshot() {
        return slingshot;
    }

    public Array<Block> getBlocks() {
        return blocks;
    }

    public Array<Pig> getPigs() {
        return pigs;
    }

    public Array<Bird> getActiveBirds() {
        return activeBirds;
    }

    // --- Save/restore support for bird queue progress ---
    // currentBirdIndex indicates how many birds have been spawned from the queue.
    // The currently attached bird (if any) is always the one referenced by slingshot.
    public int getCurrentBirdIndex() {
        return currentBirdIndex;
    }

    public void setCurrentBirdIndex(int currentBirdIndex) {
        this.currentBirdIndex = Math.max(0, currentBirdIndex);
    }


    public LevelData getCurrentLevel() {
        return currentLevel;
    }

    public LevelLoader getLevelLoader() {
        return levelLoader;
    }
}
