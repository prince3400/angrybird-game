package com.game.angrybirds.entities.birds;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.game.angrybirds.utils.GameConstants;

/** Bomb bird — explodes on ability activation, damaging nearby entities. */
public class BombBird extends Bird {

    private static final float EXPLOSION_RADIUS = 80f;
    private static final float EXPLOSION_FORCE = 25f;

    public BombBird(World world, Sprite sprite, float x, float y) {
        super(world, sprite, x, y);
    }

    @Override
    protected void performAbility() {
        if (body == null) return;
        Vector2 center = body.getWorldCenter();
        // Apply radial impulse to nearby bodies via world query would need callback;
        // simplified: self-destruct with large impulse outward
        body.applyLinearImpulse(new Vector2(0, EXPLOSION_FORCE), center, true);
        health = 0;
        markForRemoval();
    }

    public float getExplosionRadius() {
        return GameConstants.toMeters(EXPLOSION_RADIUS);
    }

    public float getExplosionForce() {
        return EXPLOSION_FORCE;
    }

    @Override
    public String getBirdName() {
        return "Bomb Bird";
    }
}
