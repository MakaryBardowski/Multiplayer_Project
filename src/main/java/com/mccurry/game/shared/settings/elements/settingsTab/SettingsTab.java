package com.mccurry.game.shared.settings.elements.settingsTab;

import com.jme3.asset.AssetManager;
import com.mccurry.game.shared.settings.elements.UserSettingsPopulated;
import com.mccurry.game.shared.ui.RedrawableContainer;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;
import com.simsilica.lemur.Insets3f;

public abstract class SettingsTab extends RedrawableContainer implements UserSettingsPopulated {
    protected final AssetManager assetManager;

    public SettingsTab(AssetManager assetManager){
        this.assetManager = assetManager;
        this.setBackground(null);
    }

    protected Insets3f calculateInsets(UiResolutionSettings resolutionSettings) {
        var uiScale = resolutionSettings.getUiScale();
        return new Insets3f(
                5 * uiScale,
                7 * uiScale,
                5 * uiScale,
                7 * uiScale
        );
    }
}
