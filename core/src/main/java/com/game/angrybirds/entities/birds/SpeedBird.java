package com.game.angrybirds.entities.birds;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/** Speed bird — dash boost in current velocity direction on ability activation. */
public class SpeedBird extends Bird {

    private static final float DASH_MULTIPLIER = 2.5f;

    public SpeedBird(World world, Sprite sprite, float x, float y) {
        super(world, sprite, x, y);
    }

    @Override
    protected void performAbility() {
        if (body == null) return;
        Vector2 vel = body.getLinearVelocity();
        if (vel.len() < 0.1f) {
            vel.set(1, 0);
        }
        vel.nor().scl(DASH_MULTIPLIER * 8f);
        body.setLinearVelocity(vel);
    }

    @Override
    public String getBirdName() {
        return "Speed Bird";
    }
}
