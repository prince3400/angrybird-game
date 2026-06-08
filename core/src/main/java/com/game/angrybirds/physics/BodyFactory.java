package com.game.angrybirds.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.game.angrybirds.utils.GameConstants;

/**
 * Factory for creating standardized Box2D bodies with correct filters and user data hooks.
 */
public final class BodyFactory {

    private BodyFactory() {}

    public static Body createCircle(World world, float x, float y, float radius,
                                   BodyDef.BodyType type, float density,
                                   float friction, float restitution,
                                   short category, short mask, Object userData) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(GameConstants.toMeters(x), GameConstants.toMeters(y));

        Body body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(GameConstants.toMeters(radius));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.filter.categoryBits = category;
        fixtureDef.filter.maskBits = mask;

        body.createFixture(fixtureDef).setUserData(userData);
        shape.dispose();
        return body;
    }

    public static Body createBox(World world, float x, float y, float width, float height,
                                 BodyDef.BodyType type, float density,
                                 float friction, float restitution,
                                 short category, short mask, Object userData) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(GameConstants.toMeters(x), GameConstants.toMeters(y));

        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(GameConstants.toMeters(width / 2f), GameConstants.toMeters(height / 2f));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.filter.categoryBits = category;
        fixtureDef.filter.maskBits = mask;

        body.createFixture(fixtureDef).setUserData(userData);
        shape.dispose();
        return body;
    }

    public static Body createGround(World world, float x, float y, float width, float height) {
        return createBox(world, x, y, width, height,
                BodyDef.BodyType.StaticBody, 0f, 0.8f, 0.1f,
                GameConstants.CATEGORY_GROUND, GameConstants.MASK_GROUND, "ground");
    }

    public static float computeImpactImpulse(Body bodyA, Body bodyB) {
        Vector2 velA = bodyA.getLinearVelocity();
        Vector2 velB = bodyB.getLinearVelocity();
        float relVel = velA.sub(velB).len();
        float massA = bodyA.getMass();
        float massB = bodyB.getMass();
        return relVel * (massA + massB);
    }
}
