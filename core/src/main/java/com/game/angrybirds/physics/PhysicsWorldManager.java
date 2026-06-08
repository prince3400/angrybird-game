package com.game.angrybirds.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.game.angrybirds.utils.GameConstants;

/**
 * Manages the Box2D world lifecycle: creation, stepping, and disposal.
 */
public class PhysicsWorldManager implements Disposable {

    private final World world;
    private final Vector2 gravity;
    private float accumulator;

    public PhysicsWorldManager() {
        gravity = new Vector2(0, GameConstants.GRAVITY_Y);
        world = new World(gravity, true);
    }

    public World getWorld() {
        return world;
    }

    /** Fixed timestep physics update with accumulator pattern. */
    public void update(float delta) {
        accumulator += delta;
        while (accumulator >= GameConstants.TIME_STEP) {
            world.step(GameConstants.TIME_STEP, GameConstants.VELOCITY_ITERATIONS, GameConstants.POSITION_ITERATIONS);
            accumulator -= GameConstants.TIME_STEP;
        }
    }

    public void setGravity(float gravityY) {
        gravity.y = gravityY;
        world.setGravity(gravity);
    }

    public void setContactListener(com.badlogic.gdx.physics.box2d.ContactListener listener) {
        world.setContactListener(listener);
    }

    /** Removes bodies marked for destruction after physics step. */
    public void destroyBodies(Array<com.badlogic.gdx.physics.box2d.Body> bodies) {
        for (com.badlogic.gdx.physics.box2d.Body body : bodies) {
            world.destroyBody(body);
        }
        bodies.clear();
    }

    @Override
    public void dispose() {
        world.dispose();
    }
}
