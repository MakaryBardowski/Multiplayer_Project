package com.mccurry.game.gameStates.boot.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameStates.boot.events.BootUiStateSetupCompleteEvent;
import com.mccurry.game.gameStates.boot.events.InitialBootCompleteEvent;
import com.mccurry.game.gameStates.orchestrator.GameStateEventContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeOutEvent;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class LoadingScreenState extends BaseAppState implements SyncEventHandler<InitialBootCompleteEvent, GameStateEventContext> {
    private Geometry blackOverlayGeom;

    @Override
    protected void initialize(Application application) {
        var orchestrationBus = getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus();
        orchestrationBus.subscribe(InitialBootCompleteEvent.class,this);

        var simpleApplication = (SimpleApplication) application;
        var settings = simpleApplication.getContext().getSettings();

        float width = settings.getWidth();
        float height = settings.getHeight();

        var quad = new Quad(width, height);
        blackOverlayGeom = new Geometry("BlackQuad", quad);

        var mat = new Material(simpleApplication.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        blackOverlayGeom.setMaterial(mat);
        simpleApplication.getGuiNode().attachChild(blackOverlayGeom);

        System.out.println("[LoadingScreenState] Loading screen initialized");
        orchestrationBus.publish(new BootUiStateSetupCompleteEvent(), new GameStateEventContext(),null);
    }

    @Override
    protected void cleanup(Application application) {
        blackOverlayGeom.removeFromParent();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public void handle(@NonNull InitialBootCompleteEvent event, @NonNull GameStateEventContext eventContext, @Nullable EventLifecycleManager<InitialBootCompleteEvent> lifecycleManager) {
        var orchestrationBus = getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus();
        orchestrationBus.unsubscribe(InitialBootCompleteEvent.class, this);
        orchestrationBus.publish(new StartScreenFadeOutEvent(2f), new GameStateEventContext(), null);
        System.out.println("[LoadingScreenState] Detaching loading screen.");
        getStateManager().detach(this);
    }
}
