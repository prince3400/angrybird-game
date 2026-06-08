package com.game.angrybirds.entities.blocks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.game.angrybirds.assets.GameAssets;
import com.game.angrybirds.levels.BlockType;

/** Creates block instances from level block type definitions. */
public final class BlockFactory {

    private BlockFactory() {}

    public static Block create(World world, GameAssets assets, BlockType type, float x, float y) {
        switch (type) {
            case STONE:
                return new StoneBlock(world, new Sprite(assets.region(assets.blockStone)), x, y);
            case GLASS:
                return new GlassBlock(world, new Sprite(assets.region(assets.blockGlass)), x, y);
            case WOOD:
            default:
                return new WoodBlock(world, new Sprite(assets.region(assets.blockWood)), x, y);
        }
    }
}
