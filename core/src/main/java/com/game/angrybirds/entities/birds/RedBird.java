package com.game.angrybirds.entities.birds;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;

/** Standard red bird — balanced damage, no special ability beyond extra knockback. */
public class RedBird extends Bird {

    public RedBird(World world, Sprite sprite, float x, float y) {
        super(world, sprite, x, y);
    }

    @Override
    protected void performAbility() {
        // Red bird: burst of forward velocity on tap
        if (body != null) {
            com.badlogic.gdx.math.Vector2 vel = body.getLinearVelocity();
            vel.scl(1.4f);
            body.setLinearVelocity(vel);
        }
    }

    @Override
    public String getBirdName() {
        return "Red Bird";
    }
}
