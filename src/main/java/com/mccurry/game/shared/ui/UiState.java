package com.mccurry.game.shared.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.event.MainThreadRefreshUiEvent;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;
import com.mccurry.game.shared.ui.service.UiResolutionSettingsService;
import lombok.NonNull;


public abstract class UiState extends BaseAppState implements UiService, EventSubscriber<GameOrchestrationContext> {
    public static final String DATA_KEY_BASE_FONT_SIZE = "baseFontSize";

    private static final UiResolutionSettingsService UI_RESOLUTION_SETTINGS_SERVICE = new UiResolutionSettingsService();
    private final SyncEventHandler<MainThreadRefreshUiEvent,GameOrchestrationContext> recalculateUiHandler = getRecalculateUiHandler();

    private final Node stateGuiNode = new Node(
            "%s-$%s-stateNode".formatted(this.getClass().getSimpleName(),System.identityHashCode(this))
    );

    @Override
    protected final void initialize(Application application) {
        System.out.println("[%s] Attached.".formatted(this.getClass().getSimpleName()));

        var orchestrator = application.getStateManager().getState(GameStateOrchestrator.class);
        var orchestrationBus = orchestrator.getGameOrchestrationBus();

        var currentUserSettings = orchestrator.getUserSettingsService().getCurrentSettings();
        var uiResolutionSettings = UI_RESOLUTION_SETTINGS_SERVICE.getFor(currentUserSettings);

        var simpleApp = (SimpleApplication) application;

        createUi(simpleApp, stateGuiNode, uiResolutionSettings);

        layoutUi(uiResolutionSettings);

        subscribe(orchestrationBus);
        simpleApp.getGuiNode().attachChild(stateGuiNode);
    }

    public static UiResolutionSettings getResolutionSettingsFor(UserSettings userSettings) {
        return UI_RESOLUTION_SETTINGS_SERVICE.getFor(userSettings);
    }

    @Override
    protected void cleanup(Application application) {
        stateGuiNode.detachAllChildren();
        stateGuiNode.removeFromParent();
        unsubscribe(application.getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus());
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}

    @Override
    public final void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(MainThreadRefreshUiEvent.class, recalculateUiHandler);
    }

    @Override
    public final void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(MainThreadRefreshUiEvent.class, recalculateUiHandler);
    }


    private SyncEventHandler<MainThreadRefreshUiEvent, GameOrchestrationContext> getRecalculateUiHandler(){
        var menuState = this;
        return (event, eventContext, lifecycleManager) -> {
            var uiResolutionSettings = getResolutionSettingsFor(event.getUserSettingsCopy());
            menuState.layoutUi(uiResolutionSettings);
        };
    }
}
