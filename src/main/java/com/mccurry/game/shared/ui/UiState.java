package com.mccurry.game.shared.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.ui.event.MainThreadRefreshUiEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public abstract class UiState extends BaseAppState implements UiService, Refreshable, SyncEventHandler<MainThreadRefreshUiEvent, GameOrchestrationContext> {

    private final Node stateGuiNode = new Node(
            "%s-$%s-stateNode".formatted(this.getClass().getSimpleName(),System.identityHashCode(this))
    );

    @Override
    protected final void initialize(Application application) {
        System.out.println("[%s] Attached.".formatted(this.getClass().getSimpleName()));

        var orchestrator = application.getStateManager().getState(GameStateOrchestrator.class);
        orchestrator.getGameOrchestrationBus().subscribe(MainThreadRefreshUiEvent.class,this);

        var simpleApp = (SimpleApplication) application;

        createUi(simpleApp, stateGuiNode);
        refresh();
        simpleApp.getGuiNode().attachChild(stateGuiNode);
    }

    @Override
    protected void cleanup(Application application) {
        var orchestrator = application.getStateManager().getState(GameStateOrchestrator.class);
        orchestrator.getGameOrchestrationBus().unsubscribe(MainThreadRefreshUiEvent.class,this);

        stateGuiNode.detachAllChildren();
        stateGuiNode.removeFromParent();
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}

    @Override
    public final void handle(@NonNull MainThreadRefreshUiEvent event, @NonNull GameOrchestrationContext eventContext, @Nullable EventLifecycleManager<MainThreadRefreshUiEvent> lifecycleManager) {
        refresh();
    }
}
