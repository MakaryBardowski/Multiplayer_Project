package com.mccurry.game.shared.settings.elements;

import com.jme3.asset.AssetManager;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.shared.settings.elements.option.OptionDropdownSelector;
import com.mccurry.game.shared.settings.event.MainThreadResolutionChangedEvent;
import com.mccurry.game.shared.settings.model.ResolutionSettings;
import com.mccurry.game.shared.ui.RedrawableContainer;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;
import com.mccurry.game.shared.ui.util.ContainerUtil;
import com.simsilica.lemur.*;
import com.simsilica.lemur.core.VersionedList;

import java.util.ArrayList;
import java.util.Arrays;

public class GraphicsSettingsTab extends RedrawableContainer {

    private final OptionDropdownSelector<ResolutionSettings> resolutionOption;

    private Container paddingContainer;


    public GraphicsSettingsTab(
            EventBus<GameOrchestrationContext> orchestrationBus,
            AssetManager assetManager
    ) {
        this.setBackground(null);

        var resolutionOptions = new ArrayList<>(Arrays.stream(ResolutionSettings.values()).toList());
        resolutionOption = new OptionDropdownSelector<>(
                "Resolution",
                VersionedList.wrap(
                        resolutionOptions
                ),
                ro -> ro.name().replace("R_", ""),
                6,
                source -> {
                    var selectedItem = (ResolutionSettings) source.getSelectedItem();
                    orchestrationBus.publish(new MainThreadResolutionChangedEvent(selectedItem), new GameOrchestrationContext(), null);
                },
                assetManager
        );

        this.addChild(resolutionOption);

        paddingContainer = ContainerUtil.noBackgroundContainer();

        this.addChild(paddingContainer);
    }

    @Override
    public void resizeForResolution(UiResolutionSettings resolutionSettings) {
        var size = resolutionSettings.getSizeFor(360, 135-18, 1);
        this.setPreferredSize(size);

        var insets = calculateInsets(resolutionSettings);
        this.setInsets(insets);

        resolutionOption.resizeForResolution(resolutionSettings);

        paddingContainer.setPreferredSize(
                this.getPreferredSize().clone().subtractLocal(
                        0,
                        resolutionOption.getMutableLabelSize().getY() + insets.getMinInsets().getY() + insets.getMaxInsets().getY(),
                        0
                )
        );
    }

    private Insets3f calculateInsets(UiResolutionSettings resolutionSettings) {
        var uiScale = resolutionSettings.getUiScale();
        return new Insets3f(
                5 * uiScale,
                7 * uiScale,
                5 * uiScale,
                7 * uiScale
        );
    }
}
