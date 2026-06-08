package com.game.angrybirds;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.game.angrybirds.utils.GameConstants;

/** Desktop entry point for LWJGL3 (Mac M1 compatible). */
public class DesktopLauncher {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Angry Birds Clone");
        config.setWindowedMode((int) GameConstants.WORLD_WIDTH, (int) GameConstants.WORLD_HEIGHT);
        config.useVsync(true);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new AngryBirdsGame(), config);
    }
}
