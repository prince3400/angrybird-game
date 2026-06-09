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

    // Visual alignment: Entity.syncSpriteToBody already centers sprite on the Box2D body.
    // If the pig PNG has transparent padding, the visible pig may appear to "hover".
    // Use a small fixed pixel offset to keep the visual aligned.
    // private static final float VISUAL_OFFSET_Y_PIXELS = -5f; // negative = down in LibGDX Y

    // Box2D bodies cannot change type while the world is locked (during contact callbacks).
    // Defer this switch until update() after the physics step.
    private boolean switchToDynamicOnUpdate;

    public Pig(World world, Sprite sprite, float x, float y) {
        super(createBody(world, x, y), sprite);

        // Ensure perfect center anchoring so syncSpriteToBody() aligns sprite center to the Box2D body's center.
        if (this.sprite != null) {
            this.sprite.setOrigin(this.sprite.getWidth() / 2f, this.sprite.getHeight() / 2f);
        }

        // Hard clamp immediately after spawn to prevent solver jitter and ensure the pig starts at rest.
        if (body != null) {
            body.setLinearVelocity(0f, 0f);
            body.setAngularVelocity(0f);
            body.setAwake(false);
        }
    }


    private static Body createBody(World world, float x, float y) {
        // Start as static so it can properly rest on blocks and never drift/slip at spawn.
        // Switch to dynamic only when the pig is destroyed.
        return BodyFactory.createCircle(
                world, x, y, GameConstants.PIG_RADIUS,
                BodyDef.BodyType.StaticBody,
                0.8f, 0.5f, 0.3f,
                GameConstants.CATEGORY_PIG,
                GameConstants.MASK_PIG,
                null
        );
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // Defer Box2D body type changes until after the world step is complete.
        if (switchToDynamicOnUpdate && body != null) {
            body.setType(BodyDef.BodyType.DynamicBody);
            body.setAwake(true);
            switchToDynamicOnUpdate = false;
        }

        // Prevent upward motion (pigs should never "fly" upward on spawn).
        // Keep horizontal velocity intact.
        if (body != null) {
            Vector2 v = body.getLinearVelocity();
            if (v.y > 0f) {
                body.setLinearVelocity(v.x, 0f);
            }
        }

        // Visual alignment compensation: keep the rendered pig snug against the
        // Box2D contact point even if the PNG has transparent padding.
        // if (sprite != null) {
        //     sprite.setPosition(sprite.getX(), sprite.getY() + VISUAL_OFFSET_Y_PIXELS);
        // }

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

        // Keep death pop mostly horizontal so pigs don't look like they "fly".
        // Defer body type switch to update() to avoid Box2D world-locked assertion.
        if (body != null) {
            switchToDynamicOnUpdate = true;

            body.applyLinearImpulse(
                    new Vector2(
                            (float) (Math.random() - 0.5) * GameConstants.PIG_DEATH_IMPULSE * 0.35f,
                            GameConstants.PIG_DEATH_IMPULSE * 0.02f
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
