package com.game.angrybirds.ui;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.game.angrybirds.assets.GameAssets;

/**
 * Builds reusable Scene2D UI widgets with consistent styling.
 */
public class UIFactory {

    private UIFactory() {}

    public static TextButton createButton(String text, GameAssets assets, Runnable onClick) {
        Skin skin = createDefaultSkin(assets);
        TextButton button = new TextButton(text, skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onClick.run();
            }
        });
        return button;
    }

    public static Skin createDefaultSkin(GameAssets assets) {
        // Skin skin = new Skin();
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        skin.add("default-font", assets.font);
        com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle style =
                new com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle();
        style.font = assets.font;
        Drawable buttonBg = new TextureRegionDrawable(assets.region(assets.uiButton));
        style.up = buttonBg;
        style.down = buttonBg;
        style.over = buttonBg;
        skin.add("default", style);
        return skin;
    }

    public static Table createMenuTable() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        return table;
    }
}
