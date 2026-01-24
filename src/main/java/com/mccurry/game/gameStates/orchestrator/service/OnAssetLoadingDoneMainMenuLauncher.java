package com.mccurry.game.gameStates.orchestrator.service;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.boot.events.AssetLoadingCompleteEvent;
import com.mccurry.game.gameStates.menu.main.event.MenuOpenRequest;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.shared.ui.MenuType;
import lombok.NonNull;


public class OnAssetLoadingDoneMainMenuLauncher implements SyncEventHandler<AssetLoadingCompleteEvent, GameOrchestrationContext> {
    private final Runnable onBootDoneRunnable;

    public OnAssetLoadingDoneMainMenuLauncher(
            @NonNull EventBus<GameOrchestrationContext> eventBus
    ){
        onBootDoneRunnable = () -> {
            System.out.println("[MainMenuLauncherHandler] Launching main menu");
            eventBus.unsubscribe(AssetLoadingCompleteEvent.class,this);
            eventBus.publish(
                    MenuOpenRequest.builder()
                            .menuType(MenuType.MAIN_MENU)
                            .build(),
                    new GameOrchestrationContext(),
                    null
            );
        };
    }

    @Override
    public void handle(@NonNull AssetLoadingCompleteEvent event, @NonNull GameOrchestrationContext eventContext, @org.jetbrains.annotations.Nullable EventLifecycleManager<AssetLoadingCompleteEvent> lifecycleManager) {
        onBootDoneRunnable.run();
    }
}
