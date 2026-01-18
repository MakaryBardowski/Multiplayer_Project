package com.mccurry.game.shared.settings.elements.settingsTab;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.shared.settings.elements.option.OptionDropdownSelector;
import com.mccurry.game.shared.settings.event.MainThreadResolutionChangedEvent;
import com.mccurry.game.shared.settings.model.ResolutionSettings;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.util.ContainerUtil;
import com.simsilica.lemur.*;

import java.util.ArrayList;
import java.util.Arrays;

import static com.mccurry.game.shared.settings.elements.option.OptionSelector.OPTION_CONTAINER_INSETS_TOP_BOTTOM;

public class GraphicsSettingsTab extends SettingsTab {

    private final OptionDropdownSelector<ResolutionSettings> resolutionOption;

    private Container paddingContainer;

    public GraphicsSettingsTab(
            EventBus<GameOrchestrationContext> orchestrationBus,
            AssetManager assetManager
    ) {
        super(assetManager);

        var resolutionOptions = new ArrayList<>(Arrays.stream(ResolutionSettings.values()).toList());
        resolutionOption = new OptionDropdownSelector<>(
                "Resolution",
                        resolutionOptions
                ,
                ro -> ro.name().replace("R_", ""),
                6,
                source -> {
                    var selectedItem = (ResolutionSettings) source.getSelectedItem();
                    System.err.println("selected "+selectedItem);
                    orchestrationBus.publish(new MainThreadResolutionChangedEvent(selectedItem), new GameOrchestrationContext(), null);
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
    public void populateWithSettings(UserSettings userSettings) {
        resolutionOption.setSelectedItem(userSettings.getResolutionSetting());
    }
}
