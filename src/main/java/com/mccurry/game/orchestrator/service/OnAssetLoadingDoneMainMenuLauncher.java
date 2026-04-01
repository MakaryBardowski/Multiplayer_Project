package com.mccurry.game.orchestrator.service;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.boot.events.AssetLoadingCompleteEvent;
import com.mccurry.game.menu.main.event.OpenMenuRequestEvent;
import com.mccurry.game.shared.ui.MenuType;
import lombok.NonNull;


public class OnAssetLoadingDoneMainMenuLauncher implements SyncEventHandler<AssetLoadingCompleteEvent> {
    private final Runnable onBootDoneRunnable;

    public OnAssetLoadingDoneMainMenuLauncher(
            @NonNull EventBus eventBus
    ){
        onBootDoneRunnable = () -> {
            System.out.println("[MainMenuLauncherHandler] Launching main menu");
            eventBus.unsubscribe(AssetLoadingCompleteEvent.class,this);
            eventBus.publish(
                    OpenMenuRequestEvent.builder()
                            .menuType(MenuType.MAIN_MENU)
                            .build(),
                    null
            );
        };
    }

    @Override
    public void handle(@NonNull AssetLoadingCompleteEvent event, @org.jetbrains.annotations.Nullable EventLifecycleManager<AssetLoadingCompleteEvent> lifecycleManager) {
        onBootDoneRunnable.run();
    }
}
