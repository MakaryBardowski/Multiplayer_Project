package com.mccurry.game.shared.ui.util;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.texture.Texture;

public class FontUtil {

    public static BitmapFont loadPixelPerfectFont(AssetManager assetManager, String fontPath){
        var font = assetManager.loadFont(fontPath);
        Texture fontTex =  font.getPage(0).getTextureParam("ColorMap").getTextureValue();
        fontTex.setMagFilter(Texture.MagFilter.Nearest);
        fontTex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        return font;
    }
}
