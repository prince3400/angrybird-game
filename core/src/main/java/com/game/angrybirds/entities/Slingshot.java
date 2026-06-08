package com.game.angrybirds.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.game.angrybirds.entities.birds.Bird;
import com.game.angrybirds.utils.GameConstants;
import com.game.angrybirds.utils.MathUtils;

/**
 * Slingshot controller: handles drag, release, trajectory preview, and bird attachment.
 */
public class Slingshot {

    private final Vector2 anchor = new Vector2(GameConstants.SLINGSHOT_X, GameConstants.SLINGSHOT_Y);
    private final Vector2 dragEnd = new Vector2();
    private final Vector2 dragVector = new Vector2();
    private final Sprite frameSprite;

    private Bird attachedBird;
    private boolean dragging;

    public Slingshot(Sprite frameSprite) {
        this.frameSprite = frameSprite;
        if (frameSprite != null) {
            frameSprite.setPosition(anchor.x - frameSprite.getWidth() / 2f, anchor.y - 20);
        }
    }

    public void attachBird(Bird bird) {
        this.attachedBird = bird;
        if (bird != null) {
            bird.attachToSlingshot(anchor.x, anchor.y + GameConstants.BIRD_RADIUS);
        }
    }

    public Bird getAttachedBird() {
        return attachedBird;
    }

    public void startDrag(float screenX, float screenY) {
        if (attachedBird == null) return;
        dragging = true;
        attachedBird.prepareForLaunch();
        updateDrag(screenX, screenY);
    }

    public void updateDrag(float screenX, float screenY) {
        if (!dragging || attachedBird == null) return;
        dragEnd.set(screenX, screenY);
        dragVector.set(anchor).sub(dragEnd);
        MathUtils.clampDrag(dragVector, GameConstants.SLINGSHOT_MAX_DRAG);

        float birdX = anchor.x - dragVector.x;
        float birdY = anchor.y - dragVector.y + GameConstants.BIRD_RADIUS;
        attachedBird.attachToSlingshot(birdX, birdY);
    }

    public Vector2 release() {
        if (!dragging || attachedBird == null) {
            dragging = false;
            return null;
        }
        dragging = false;
        Vector2 launchVel = MathUtils.computeLaunchVelocity(dragVector);
        attachedBird.launch(launchVel);
        Bird launched = attachedBird;
        attachedBird = null;
        return launchVel;
    }

    public boolean isDragging() {
        return dragging;
    }

    public Vector2 getAnchor() {
        return anchor;
    }

    public Vector2 getDragVector() {
        return dragVector;
    }

    /** Returns predicted trajectory points for rendering dots. */
    public Vector2[] getTrajectoryPoints() {
        if (!dragging || attachedBird == null) return new Vector2[0];

        Vector2 launchVel = MathUtils.computeLaunchVelocity(dragVector);
        Vector2 origin = new Vector2(anchor.x, anchor.y + GameConstants.BIRD_RADIUS);
        Vector2[] points = new Vector2[GameConstants.TRAJECTORY_DOT_COUNT];

        for (int i = 0; i < GameConstants.TRAJECTORY_DOT_COUNT; i++) {
            float t = (i + 1) * GameConstants.TRAJECTORY_DOT_INTERVAL;
            points[i] = MathUtils.predictPosition(origin, launchVel, t);
        }
        return points;
    }

    public void render(Batch batch) {
        if (frameSprite != null) {
            frameSprite.draw(batch);
        }
    }

    public void renderTrajectory(Batch batch, Sprite dotSprite) {
        if (!dragging || dotSprite == null) return;
        for (Vector2 point : getTrajectoryPoints()) {
            dotSprite.setPosition(point.x - dotSprite.getWidth() / 2f, point.y - dotSprite.getHeight() / 2f);
            dotSprite.draw(batch);
        }
    }
}
