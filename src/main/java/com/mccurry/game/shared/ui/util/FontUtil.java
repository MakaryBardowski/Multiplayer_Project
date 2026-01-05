package com.mccurry.game.shared.ui.util;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;

import static com.mccurry.game.shared.ui.UiState.DATA_KEY_BASE_FONT_SIZE;

public class FontUtil {
    public static float getFontSize(Node node, UiResolutionSettings resolutionSettings){
        return (Float) node.getUserData(DATA_KEY_BASE_FONT_SIZE)*resolutionSettings.getUiScale();
    }

    public static BitmapFont loadPixelPerfectFont(AssetManager assetManager, String fontPath){
        var font = assetManager.loadFont(fontPath);
        Texture fontTex =  font.getPage(0).getTextureParam("ColorMap").getTextureValue();
        fontTex.setMagFilter(Texture.MagFilter.Nearest);
        fontTex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        return font;
    }
}
