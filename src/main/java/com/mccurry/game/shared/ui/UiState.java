package com.mccurry.game.shared.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;
import com.mccurry.game.shared.ui.service.UiResolutionSettingsService;


public abstract class UiState extends BaseAppState implements UiService, EventSubscriber<GameOrchestrationContext> {
    private static final UiResolutionSettingsService UI_RESOLUTION_SETTINGS_SERVICE = new UiResolutionSettingsService();
    private final Node stateGuiNode = new Node(
            "%s-$%s-stateNode".formatted(this.getClass().getSimpleName(),System.identityHashCode(this))
    );

    @Override
    protected final void initialize(Application application) {
        System.out.println("[%s] Attached.".formatted(this.getClass().getSimpleName()));

        var currentUserSettings = application.getStateManager().getState(GameStateOrchestrator.class).getUserSettingsService().getCurrentSettings();
        var uiResolutionSettings = UI_RESOLUTION_SETTINGS_SERVICE.getFor(currentUserSettings);

        var simpleApp = (SimpleApplication) application;

        doInitialize(simpleApp, stateGuiNode ,uiResolutionSettings);

        simpleApp.getGuiNode().attachChild(stateGuiNode);
    }

    protected abstract void doInitialize(SimpleApplication application, Node stateGuiNode, UiResolutionSettings uiResolutionSettings);

    public static UiResolutionSettings getResolutionSettingsFor(UserSettings userSettings) {
        return UI_RESOLUTION_SETTINGS_SERVICE.getFor(userSettings);
    }

    @Override
    protected void cleanup(Application application) {
        stateGuiNode.removeFromParent();
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}
}
