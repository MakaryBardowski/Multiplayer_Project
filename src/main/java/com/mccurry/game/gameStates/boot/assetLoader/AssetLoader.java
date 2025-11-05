package com.mccurry.game.gameStates.boot.assetLoader;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.AsyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameStates.boot.events.AssetLoadingPercentAdvanceEvent;
import com.mccurry.game.gameStates.boot.events.BootUiStateSetupCompleteEvent;
import com.mccurry.game.gameStates.boot.events.AssetLoadingCompleteEvent;
import com.mccurry.game.gameStates.orchestrator.GameStateEventContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class AssetLoader implements AsyncEventHandler<BootUiStateSetupCompleteEvent,GameStateEventContext> {
    private final EventBus<GameStateEventContext> orchestrationBus;

    @Override
    public void handle(@NonNull BootUiStateSetupCompleteEvent event, @NonNull GameStateEventContext eventContext, @Nullable EventLifecycleManager<BootUiStateSetupCompleteEvent> lifecycleManager) {
        System.out.println("[AssetLoader] Beginning to load assets");
        Thread.ofVirtual().start(
                () -> {
                    loadAssets();
                    orchestrationBus.unsubscribe(BootUiStateSetupCompleteEvent.class, this);
                    System.out.println("[AssetLoader] Asset loading finished. ");
                    orchestrationBus.publish(
                            new AssetLoadingCompleteEvent(),
                            new GameStateEventContext(),
                            null
                    );

                    if(lifecycleManager != null){
                        lifecycleManager.cleanupEvent(event);
                    }
                }
        );
    }

    private void loadAssets(){
        try {
            for (int i = 0; i <= 25; i++) {
                Thread.sleep(50);
                float percent = i*4;
                var normalizedPercent = percent/100;
                orchestrationBus.publish(
                        new AssetLoadingPercentAdvanceEvent(normalizedPercent),
                        new GameStateEventContext(),
                        null
                );
                System.out.println("[AssetLoader] Loading... %s%%".formatted(percent));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
