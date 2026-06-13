package com.game.angrybirds.entities.blocks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.game.angrybirds.entities.Entity;
import com.game.angrybirds.levels.BlockType;
import com.game.angrybirds.physics.BodyFactory;
import com.game.angrybirds.utils.GameConstants;

/**
 * Destructible block with material-based durability.
 */
public abstract class Block extends Entity {

    public float durability; // Changed to public for serialization
    public float maxDurability; // Changed to public for serialization
    protected BlockType blockType;

    public Block(World world, Sprite sprite, float x, float y, BlockType type,
                 float durability, float density, float friction, float restitution) {
        super(createBody(world, x, y, density, friction, restitution), sprite);
        this.blockType = type;
        this.maxDurability = durability;
        this.durability = durability;
    }

    private static Body createBody(World world, float x, float y,
                                   float density, float friction, float restitution) {
        // Entity base class sets body userData to `this`, so fixture userData can remain null.
        // Contact listener uses body userData, so we pass null for fixture userData here.
        return BodyFactory.createBox(
                world, x, y,
                GameConstants.BLOCK_WIDTH, GameConstants.BLOCK_HEIGHT,
                BodyDef.BodyType.DynamicBody,
                density, friction, restitution,
                GameConstants.CATEGORY_BLOCK,
                GameConstants.MASK_BLOCK,
                null
        );
    }

    public void takeDamage(float amount) {
        if (!alive) return;
        durability -= amount;
        if (durability <= 0) {
            markForRemoval();
        }
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public float getDurabilityPercent() {
        return durability / maxDurability;
    }
}
