package com.mccurry.game.shared.settings.elements.settingsTab;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.locale.LocalizedString;
import com.mccurry.game.shared.settings.elements.option.OptionDropdownSelector;
import com.mccurry.game.shared.settings.model.ResolutionSettings;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.util.ContainerUtil;
import com.simsilica.lemur.*;

import java.util.ArrayList;
import java.util.Arrays;

import static com.mccurry.game.shared.settings.elements.settingsTab.SettingsTabLocaleKeys.GRAPHICS_RESOLUTION;


public class GraphicsSettingsTab extends SettingsTab {

    private UserSettings temporaryUserSettings;

    private final OptionDropdownSelector<ResolutionSettings> resolutionOption;
    private Container paddingContainer;

    public GraphicsSettingsTab(
            AssetManager assetManager
    ) {
        super(assetManager);

        var resolutionOptions = new ArrayList<>(Arrays.stream(ResolutionSettings.values()).toList());
        resolutionOption = new OptionDropdownSelector<>(
                LocalizedString.get(GRAPHICS_RESOLUTION),
                        resolutionOptions
                ,
                ro -> ro.name().replace("R_", ""),
                6,
                source -> {
                    var selectedItem = (ResolutionSettings) source.getSelectedItem();
                    temporaryUserSettings.setResolutionSetting(selectedItem);
                },
                assetManager
        );

        this.addChild(resolutionOption);

        paddingContainer = ContainerUtil.noBackgroundContainer();

        this.addChild(paddingContainer);

        layout();
    }

    private void layout() {
        var size = new Vector3f(360, 135-18, 1);
        this.setPreferredSize(size);

        var insets = getTabInsets();
        this.setInsets(insets);

        paddingContainer.setPreferredSize(
                this.getPreferredSize().clone().subtractLocal(
                        0,
                        resolutionOption.getPreferredSize().getY() + insets.getMinInsets().getY() + insets.getMaxInsets().getY(),
                        0
                )
        );
    }

    @Override
    public void populateWithSettings(UserSettings temporaryUserSettings) {
        this.temporaryUserSettings = temporaryUserSettings;
        resolutionOption.setSelectedItem(this.temporaryUserSettings.getResolutionSetting());
    }
}
