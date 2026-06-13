package com.game.angrybirds.entities.birds;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.game.angrybirds.entities.Entity;
import com.game.angrybirds.physics.BodyFactory;
import com.game.angrybirds.utils.GameConstants;

/**
 * Abstract base for all bird projectiles with launch state and special abilities.
 */
public abstract class Bird extends Entity {

    public enum LaunchState {
        ON_SLINGSHOT,
        DRAGGING,
        LAUNCHED,
        SETTLED
    }

    public float health = 100f; // Changed to public for serialization
    public LaunchState launchState = LaunchState.ON_SLINGSHOT; // Changed to public for serialization
    public boolean abilityUsed; // Changed to public for serialization
    protected float settleTimer;

    public Bird(World world, Sprite sprite, float x, float y) {
        super(createBody(world, x, y), sprite);
        body.setType(BodyDef.BodyType.KinematicBody);
    }

    private static Body createBody(World world, float x, float y) {
        return BodyFactory.createCircle(
                world, x, y, GameConstants.BIRD_RADIUS,
                BodyDef.BodyType.DynamicBody,
                GameConstants.BIRD_DENSITY,
                GameConstants.BIRD_FRICTION,
                GameConstants.BIRD_RESTITUTION,
                GameConstants.CATEGORY_BIRD,
                GameConstants.MASK_BIRD,
                null
        );
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (launchState == LaunchState.LAUNCHED && body != null) {
            Vector2 vel = body.getLinearVelocity();
            if (vel.len() < GameConstants.BIRD_SETTLE_VELOCITY) {
                settleTimer += delta;
                if (settleTimer >= GameConstants.BIRD_SETTLE_TIME) {
                    launchState = LaunchState.SETTLED;
                }
            } else {
                settleTimer = 0f;
            }
        }
    }

    public void prepareForLaunch() {
        launchState = LaunchState.DRAGGING;
        body.setType(BodyDef.BodyType.KinematicBody);
        body.setAwake(true);
    }

    public void launch(Vector2 velocity) {
        launchState = LaunchState.LAUNCHED;
        body.setType(BodyDef.BodyType.DynamicBody);
        body.setLinearVelocity(velocity);
        body.applyLinearImpulse(velocity, body.getWorldCenter(), true);
    }

    public void attachToSlingshot(float x, float y) {
        launchState = LaunchState.ON_SLINGSHOT;
        body.setType(BodyDef.BodyType.KinematicBody);
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
        body.setTransform(GameConstants.toMeters(x), GameConstants.toMeters(y), 0);
    }

    public void takeDamage(float impulse) {
        health -= impulse * 10f;
        if (health <= 0) {
            markForRemoval();
        }
    }

    /** Called when bird is mid-flight; override for special abilities. */
    public void activateAbility() {
        if (abilityUsed || launchState != LaunchState.LAUNCHED) return;
        abilityUsed = true;
        performAbility();
    }

    protected abstract void performAbility();

    public abstract String getBirdName();

    public Bird.LaunchState getLaunchState() {
        return launchState;
    }

    public void setLaunchState(LaunchState state) {
        this.launchState = state;
    }

    public boolean isLaunched() {
        return launchState == LaunchState.LAUNCHED || launchState == LaunchState.SETTLED;
    }

    public boolean isSettled() {
        return launchState == LaunchState.SETTLED;
    }
}
