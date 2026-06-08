package com.game.angrybirds.entities.pigs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.game.angrybirds.entities.Entity;
import com.game.angrybirds.physics.BodyFactory;
import com.game.angrybirds.utils.GameConstants;

/**
 * Enemy pig with health and death animation state.
 */
public class Pig extends Entity {

    private float health = 50f;
    private float deathTimer;
    private boolean dying;

    public Pig(World world, Sprite sprite, float x, float y) {
        super(createBody(world, x, y), sprite);
    }

    private static Body createBody(World world, float x, float y) {
        return BodyFactory.createCircle(
                world, x, y, GameConstants.PIG_RADIUS,
                BodyDef.BodyType.DynamicBody,
                0.8f, 0.5f, 0.3f,
                GameConstants.CATEGORY_PIG,
                GameConstants.MASK_PIG,
                null
        );
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (dying) {
            deathTimer += delta;
            if (sprite != null) {
                float alpha = Math.max(0, 1f - deathTimer);
                sprite.setColor(1f, 1f, 1f, alpha);
            }
            if (deathTimer > 0.5f) {
                markForRemoval();
            }
        }
    }

    public void takeDamage(float amount) {
        if (!alive || dying) return;
        health -= amount;
        if (health <= 0) {
            startDeath();
        }
    }

    private void startDeath() {
        dying = true;
        alive = false;
        if (body != null) {
            body.applyLinearImpulse(
                    new Vector2(
                            (float) (Math.random() - 0.5) * GameConstants.PIG_DEATH_IMPULSE,
                            GameConstants.PIG_DEATH_IMPULSE
                    ),
                    body.getWorldCenter(), true
            );
        }
    }

    public boolean isDying() {
        return dying;
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.Batch batch) {
        if (sprite != null && (alive || dying)) {
            sprite.draw(batch);
            if (!dying) {
                sprite.setColor(Color.WHITE);
            }
        }
    }
}
