package com.game.angrybirds.entities.birds;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.game.angrybirds.assets.GameAssets;
import com.game.angrybirds.levels.BirdType;
import com.game.angrybirds.utils.GameConstants;

/**
 * Factory for creating bird instances from level bird type definitions.
 */
public final class BirdFactory {

    private BirdFactory() {}

    public static Bird create(World world, GameAssets assets, BirdType type, float x, float y) {
        switch (type) {
            case BOMB:
                return new BombBird(world, sizedSprite(assets.region(assets.birdBomb)), x, y);
            case SPEED:
                return new SpeedBird(world, sizedSprite(assets.region(assets.birdSpeed)), x, y);
            case RED:
            default:
                return new RedBird(world, sizedSprite(assets.region(assets.birdRed)), x, y);
        }
    }

    private static Sprite sizedSprite(com.badlogic.gdx.graphics.g2d.TextureRegion region) {
        Sprite sprite = new Sprite(region);
        // Match sprite size to Box2D physics radius (bird body is a circle).
        float size = GameConstants.BIRD_RADIUS * 2f;
        sprite.setSize(size, size);
        return sprite;
    }
}
