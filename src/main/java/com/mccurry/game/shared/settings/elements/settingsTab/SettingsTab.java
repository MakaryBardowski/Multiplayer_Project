package com.mccurry.game.shared.settings.elements.settingsTab;

import com.jme3.asset.AssetManager;
import com.mccurry.game.shared.settings.elements.UserSettingsPopulated;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Insets3f;

public abstract class SettingsTab extends Container implements UserSettingsPopulated {
    protected final AssetManager assetManager;

    public SettingsTab(AssetManager assetManager){
        this.assetManager = assetManager;
        this.setBackground(null);
    }

    protected Insets3f getTabInsets() {
        return new Insets3f(
                5,
                7,
                5,
                7
        );
    }
}
