package com.game.angrybirds.entities.birds;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.game.angrybirds.assets.GameAssets;
import com.game.angrybirds.levels.BirdType;

/**
 * Factory for creating bird instances from level bird type definitions.
 */
public final class BirdFactory {

    private BirdFactory() {}

    public static Bird create(World world, GameAssets assets, BirdType type, float x, float y) {
        switch (type) {
            case BOMB:
                return new BombBird(world, new Sprite(assets.region(assets.birdBomb)), x, y);
            case SPEED:
                return new SpeedBird(world, new Sprite(assets.region(assets.birdSpeed)), x, y);
            case RED:
            default:
                return new RedBird(world, new Sprite(assets.region(assets.birdRed)), x, y);
        }
    }
}
