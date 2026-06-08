package com.game.angrybirds.entities.blocks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.game.angrybirds.levels.BlockType;

/** Fragile glass block — low durability, high score. */
public class GlassBlock extends Block {

    public GlassBlock(World world, Sprite sprite, float x, float y) {
        super(world, sprite, x, y, BlockType.GLASS, 15f, 0.3f, 0.3f, 0.05f);
    }
}
