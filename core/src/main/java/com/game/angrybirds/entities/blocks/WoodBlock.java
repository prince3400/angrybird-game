package com.game.angrybirds.entities.blocks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.game.angrybirds.levels.BlockType;

/** Lightweight wood block — breaks easily. */
public class WoodBlock extends Block {

    public WoodBlock(World world, Sprite sprite, float x, float y) {
        super(world, sprite, x, y, BlockType.WOOD, 30f, 0.4f, 0.6f, 0.2f);
    }
}
