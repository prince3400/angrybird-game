package com.game.angrybirds.entities.blocks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.game.angrybirds.levels.BlockType;

/** Heavy stone block — high durability. */
public class StoneBlock extends Block {

    public StoneBlock(World world, Sprite sprite, float x, float y) {
        super(world, sprite, x, y, BlockType.STONE, 80f, 1.5f, 0.8f, 0.15f);
    }
}
