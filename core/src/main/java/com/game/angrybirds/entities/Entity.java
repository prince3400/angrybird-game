package com.game.angrybirds.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Disposable;
import com.game.angrybirds.utils.GameConstants;

/**
 * Base class for all physics-backed game objects.
 */
public abstract class Entity implements Disposable {

    protected Body body;
    protected Sprite sprite;
    protected boolean alive = true;
    protected boolean markedForRemoval;

    public Entity(Body body, Sprite sprite) {
        this.body = body;
        this.sprite = sprite;
        if (body != null) {
            body.setUserData(this);
        }
    }

    public void update(float delta) {
        syncSpriteToBody();
    }

    protected void syncSpriteToBody() {
        if (body == null || sprite == null) return;
        Vector2 pos = body.getPosition();
        sprite.setPosition(
                GameConstants.toPixels(pos.x) - sprite.getWidth() / 2f,
                GameConstants.toPixels(pos.y) - sprite.getHeight() / 2f
        );
        sprite.setRotation(body.getAngle() * com.badlogic.gdx.math.MathUtils.radiansToDegrees);
    }

    public void render(Batch batch) {
        if (sprite != null && alive) {
            sprite.draw(batch);
        }
    }

    public Body getBody() {
        return body;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void markForRemoval() {
        markedForRemoval = true;
        alive = false;
    }

    public void setBodyType(BodyDef.BodyType type) {
        if (body != null) {
            body.setType(type);
        }
    }

    @Override
    public void dispose() {
        if (sprite != null && sprite.getTexture() != null) {
            // Textures owned by GameAssets — do not dispose here
        }
    }
}
