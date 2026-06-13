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
    private final Sprite partSprite;
    private final int partOffsetX;
    private final int partOffsetY;


    private Bird attachedBird;
    private boolean dragging;

    public Slingshot(Sprite frameSprite) {
        this.frameSprite = frameSprite;
        this.partSprite = null;
        this.partOffsetX = 0;
        this.partOffsetY = 0;
        if (frameSprite != null) {
            frameSprite.setPosition(anchor.x - frameSprite.getWidth() / 2f, anchor.y - 20);
        }
    }


    /**
     * Slingshot constructor that composes slingshot.png + slingpart.png into a single sprite at runtime.
     */
    public Slingshot(Sprite frameSprite, Sprite partSprite, int partOffsetX, int partOffsetY) {
        this.frameSprite = frameSprite;
        this.partSprite = partSprite;
        this.partOffsetX = partOffsetX;
        this.partOffsetY = partOffsetY;

        if (frameSprite != null) {
            frameSprite.setPosition(anchor.x - frameSprite.getWidth() / 2f, anchor.y - 20);
        }

        // We overlay the part texture at runtime in render().
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
        // Move the bird's launch/attach position slightly upward.
        float birdY = anchor.y - dragVector.y + GameConstants.BIRD_RADIUS + 8f;

        attachedBird.attachToSlingshot(birdX, birdY);
    }

    public Vector2 release() {
        if (!dragging || attachedBird == null) {
            dragging = false;
            return null;
        }
        dragging = false;

        // Dragging vector points from anchor->dragEnd (because dragVector is anchor - dragEnd).
        // To launch "backwards" (towards the anchor) like classic Angry Birds,
        // flip the direction before converting to launch velocity.
        Vector2 launchVel = MathUtils.computeLaunchVelocity(dragVector).scl(-1f);

        attachedBird.launch(launchVel);
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

        // Trajectory prediction must match the actual launch direction.
        // release() flips the launch velocity, so we do the same here.
        Vector2 launchVel = MathUtils.computeLaunchVelocity(dragVector).scl(-1f);
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

        if (partSprite != null) {
            // Draw the part texture at the same anchor as the slingshot frame.
            float x = anchor.x - partSprite.getWidth() / 2f + partOffsetX;
            // Slightly raise sling part above the slingshot frame.
            // Move slingpart.png up a little in Y axis only.
            float y = anchor.y - 20 + partOffsetY + 12;
            partSprite.setPosition(x, y);


            partSprite.draw(batch);
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
