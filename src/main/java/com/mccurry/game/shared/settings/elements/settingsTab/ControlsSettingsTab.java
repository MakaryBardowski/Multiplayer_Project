package com.mccurry.game.shared.settings.elements.settingsTab;

import com.jme3.asset.AssetManager;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.shared.settings.elements.option.OptionDropdownSelector;
import com.mccurry.game.shared.settings.event.MainThreadResolutionChangedEvent;
import com.mccurry.game.shared.settings.model.ResolutionSettings;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;
import com.mccurry.game.shared.ui.util.ContainerUtil;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.core.VersionedList;

import java.util.ArrayList;
import java.util.Arrays;

public class ControlsSettingsTab extends SettingsTab{

    private Container paddingContainer;

    public ControlsSettingsTab(
            EventBus<GameOrchestrationContext> orchestrationBus,
            AssetManager assetManager
    ) {
        super(assetManager);

        paddingContainer = ContainerUtil.noBackgroundContainer();

        this.addChild(paddingContainer);
    }

    @Override
    public void resizeForResolution(UiResolutionSettings resolutionSettings) {
        var size = resolutionSettings.getSizeFor(360, 135-18, 1);
        this.setPreferredSize(size);

        var insets = calculateInsets(resolutionSettings);
        this.setInsets(insets);

        paddingContainer.setPreferredSize(
                this.getPreferredSize().clone().subtractLocal(
                        0,
                         insets.getMinInsets().getY() + insets.getMaxInsets().getY(),
                        0
                )
        );
    }



    @Override
    public void populateWithSettings(UserSettings userSettings) {
    }
}
