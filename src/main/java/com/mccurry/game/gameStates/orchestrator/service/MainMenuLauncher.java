package com.mccurry.game.gameStates.orchestrator.service;

import com.jme3.app.state.AppStateManager;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameStates.boot.events.AssetLoadingCompleteEvent;
import com.mccurry.game.gameStates.mainMenu.MainMenuState;
import com.mccurry.game.gameStates.orchestrator.GameStateEventContext;
import lombok.NonNull;


public class MainMenuLauncher implements SyncEventHandler<AssetLoadingCompleteEvent, GameStateEventContext> {
    private final Runnable onBootDoneRunnable;

    public MainMenuLauncher(
            @NonNull AppStateManager appStateManager,
            @NonNull EventBus<GameStateEventContext> eventBus
    ){
        onBootDoneRunnable = () -> {
            System.out.println("[MainMenuLauncherHandler] Launching main menu");
            eventBus.unsubscribe(AssetLoadingCompleteEvent.class,this);
            appStateManager.attach(new MainMenuState());
        };
    }

    @Override
    public void handle(@NonNull AssetLoadingCompleteEvent event, @NonNull GameStateEventContext eventContext, @org.jetbrains.annotations.Nullable EventLifecycleManager<AssetLoadingCompleteEvent> lifecycleManager) {
        onBootDoneRunnable.run();
    }
}
