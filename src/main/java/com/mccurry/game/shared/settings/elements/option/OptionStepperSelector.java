package com.mccurry.game.shared.settings.elements.option;

import com.jme3.asset.AssetManager;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;

public class OptionStepperSelector extends OptionSelector {

    public OptionStepperSelector(String optionName, AssetManager assetManager) {
        super(optionName,assetManager);
    }

    @Override
    public void resizeForResolution(UiResolutionSettings resolutionSettings) {
        super.resizeForResolution(resolutionSettings);
    }
}
