package com.game.angrybirds.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.game.angrybirds.entities.blocks.Block;
import com.game.angrybirds.entities.birds.Bird;
import com.game.angrybirds.entities.pigs.Pig;
import com.game.angrybirds.managers.ScoreManager;
import com.game.angrybirds.managers.SoundManager;
import com.game.angrybirds.utils.GameConstants;

/**
 * Handles Box2D contact events and delegates damage to game entities.
 */
public class GameContactListener implements ContactListener {

    public interface CollisionCallback {
        void onPigDestroyed(Pig pig);
        void onBlockDestroyed(Block block);
        void onBirdHit();
    }

    private final ScoreManager scoreManager;
    private final SoundManager soundManager;
    private CollisionCallback callback;

    public GameContactListener(ScoreManager scoreManager, SoundManager soundManager) {
        this.scoreManager = scoreManager;
        this.soundManager = soundManager;
    }

    public void setCallback(CollisionCallback callback) {
        this.callback = callback;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        resolveContact(fixtureA, fixtureB, contact);
    }

    @Override
    public void endContact(Contact contact) {
        // Not used
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Not used
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        float maxImpulse = 0f;
        for (float normalImpulse : impulse.getNormalImpulses()) {
            maxImpulse = Math.max(maxImpulse, normalImpulse);
        }

        if (maxImpulse < GameConstants.MIN_DAMAGE_IMPULSE) {
            return;
        }

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        applyDamage(fixtureA, fixtureB, maxImpulse);
    }

    private void resolveContact(Fixture a, Fixture b, Contact contact) {
        Object userDataA = a.getBody().getUserData();
        Object userDataB = b.getBody().getUserData();

        if (userDataA instanceof Bird || userDataB instanceof Bird) {
            if (callback != null) callback.onBirdHit();
            soundManager.playHit();
        }
    }

    private void applyDamage(Fixture a, Fixture b, float impulse) {
        Object dataA = a.getBody().getUserData();
        Object dataB = b.getBody().getUserData();

        applyToEntity(dataA, impulse);
        applyToEntity(dataB, impulse);
    }

    private void applyToEntity(Object userData, float impulse) {
        if (userData instanceof Pig) {
            Pig pig = (Pig) userData;
            if (pig.isAlive()) {
                pig.takeDamage(impulse * GameConstants.PIG_DAMAGE_SCALE);
                if (!pig.isAlive()) {
                    scoreManager.addPigScore();
                    soundManager.playPigDeath();
                    if (callback != null) callback.onPigDestroyed(pig);
                }
            }
        } else if (userData instanceof Block) {
            Block block = (Block) userData;
            if (block.isAlive()) {
                block.takeDamage(impulse * GameConstants.BLOCK_DAMAGE_SCALE);
                if (!block.isAlive()) {
                    scoreManager.addBlockScore(block.getBlockType());
                    soundManager.playBreak();
                    if (callback != null) callback.onBlockDestroyed(block);
                }
            }
        } else if (userData instanceof Bird) {
            Bird bird = (Bird) userData;
            bird.takeDamage(impulse);
        }
    }
}
