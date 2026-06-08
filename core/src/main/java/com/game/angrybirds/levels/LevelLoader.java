package com.game.angrybirds.levels;

import com.badlogic.gdx.utils.Array;

/**
 * Provides level definitions. Extend with JSON/file loading for production scale.
 */
public class LevelLoader {

    private final Array<LevelData> levels = new Array<>();

    public LevelLoader() {
        registerBuiltInLevels();
    }

    private void registerBuiltInLevels() {
        levels.add(createLevel1());
        levels.add(createLevel2());
        levels.add(createLevel3());
    }

    /** Tutorial level — simple tower with one pig. */
    private LevelData createLevel1() {
        LevelData level = new LevelData();
        level.name = "First Flight";
        level.index = 0;
        level.birds.add(BirdType.RED);
        level.birds.add(BirdType.RED);
        level.birds.add(BirdType.RED);

        // Ground platform blocks
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 900, 120));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 948, 120));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 996, 120));

        // Tower
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 948, 168));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.GLASS, 996, 168));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 948, 216));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 996, 216));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 972, 264));

        level.pigs.add(new LevelData.PigPlacement(972, 310));
        return level;
    }

    /** Mixed materials with bomb bird. */
    private LevelData createLevel2() {
        LevelData level = new LevelData();
        level.name = "Stone Fortress";
        level.index = 1;
        level.birds.add(BirdType.RED);
        level.birds.add(BirdType.BOMB);
        level.birds.add(BirdType.RED);

        level.blocks.add(new LevelData.BlockPlacement(BlockType.STONE, 850, 120));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.STONE, 898, 120));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.STONE, 946, 120));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.STONE, 994, 120));

        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 874, 168));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 922, 168));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.GLASS, 970, 168));

        level.blocks.add(new LevelData.BlockPlacement(BlockType.STONE, 898, 216));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.STONE, 946, 216));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 922, 264));

        level.pigs.add(new LevelData.PigPlacement(922, 310));
        level.pigs.add(new LevelData.PigPlacement(970, 168));
        return level;
    }

    /** Speed bird challenge with multiple pigs. */
    private LevelData createLevel3() {
        LevelData level = new LevelData();
        level.name = "Speed Challenge";
        level.index = 2;
        level.birds.add(BirdType.SPEED);
        level.birds.add(BirdType.RED);
        level.birds.add(BirdType.BOMB);

        for (int i = 0; i < 5; i++) {
            level.blocks.add(new LevelData.BlockPlacement(BlockType.GLASS, 800 + i * 48, 120));
        }

        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 848, 168));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 896, 168));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.STONE, 944, 168));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 848, 216));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 896, 216));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 944, 216));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 872, 264));
        level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 920, 264));

        level.pigs.add(new LevelData.PigPlacement(872, 310));
        level.pigs.add(new LevelData.PigPlacement(944, 310));
        level.pigs.add(new LevelData.PigPlacement(896, 168));
        return level;
    }

    public LevelData getLevel(int index) {
        if (index < 0 || index >= levels.size) return levels.first();
        return levels.get(index);
    }

    public int getLevelCount() {
        return levels.size;
    }

    public Array<LevelData> getAllLevels() {
        return levels;
    }
}
