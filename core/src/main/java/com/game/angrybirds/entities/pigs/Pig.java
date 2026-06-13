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

    private float health = 50f; // Changed to public for serialization
    private float deathTimer;
    public boolean dying; // Changed to public for serialization

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

        // If the pig is currently supported by a block/contact, Box2D will keep it at rest.
        // When the block falls/breaks, the pig must become dynamic and continue falling.
        // So we do NOT forcefully clamp upward velocity to 0 here (it can cause the pig
        // to appear stuck mid-air after support is removed).


        // Keep sprite in sync with physics (Entity.syncSpriteToBody is called by Entity.update())
        // This enables the pig sprite to move/rotate with the Box2D body.

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

    public void startDeath() {
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
