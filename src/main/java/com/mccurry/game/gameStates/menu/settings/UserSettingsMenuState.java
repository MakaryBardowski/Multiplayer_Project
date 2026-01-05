package com.mccurry.game.gameStates.menu.settings;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.settings.elements.UserSettingsPanel;
import com.mccurry.game.shared.ui.UiState;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;

public class UserSettingsMenuState extends UiState {
    private static final UserSettingsMenuState instance = new UserSettingsMenuState();

    private UserSettingsPanel userSettingsPanel;

    private UserSettingsMenuState(){}

    @Override
    public void createUi(SimpleApplication application, Node stateGuiNode, UiResolutionSettings uiResolutionSettings) {
        var stateManager = application.getStateManager();
        var bus = stateManager.getState(GameStateOrchestrator.class).getGameOrchestrationBus();
        userSettingsPanel = new UserSettingsPanel(bus,stateManager,application.getAssetManager());

        stateGuiNode.attachChild(userSettingsPanel);
    }

    @Override
    public void layoutUi(UiResolutionSettings uiResolutionSettings) {
        userSettingsPanel.resizeForResolution(uiResolutionSettings);
        var settingsPanelSize = userSettingsPanel.getPreferredSize();
        userSettingsPanel.setLocalTranslation(new Vector3f(
                (uiResolutionSettings.getWidth()-settingsPanelSize.getX())/2,
                (uiResolutionSettings.getHeight()+settingsPanelSize.getY())/2,
                2
        ));
    }

    public static UserSettingsMenuState getNew(){
        return instance;
    }
}
