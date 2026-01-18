package com.mccurry.game.shared.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;

public abstract class UiState extends BaseAppState implements UiService{

    private final Node stateGuiNode = new Node(
            "%s-$%s-stateNode".formatted(this.getClass().getSimpleName(),System.identityHashCode(this))
    );

    @Override
    protected final void initialize(Application application) {
        System.out.println("[%s] Attached.".formatted(this.getClass().getSimpleName()));

        var orchestrator = application.getStateManager().getState(GameStateOrchestrator.class);

        var simpleApp = (SimpleApplication) application;

        createUi(simpleApp, stateGuiNode);

        simpleApp.getGuiNode().attachChild(stateGuiNode);
    }

    @Override
    protected void cleanup(Application application) {
        stateGuiNode.detachAllChildren();
        stateGuiNode.removeFromParent();
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}
}
