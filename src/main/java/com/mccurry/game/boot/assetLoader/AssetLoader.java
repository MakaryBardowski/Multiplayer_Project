package com.mccurry.game.boot.assetLoader;

import com.jme3.app.SimpleApplication;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.AsyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.boot.events.AssetLoadingPercentAdvanceEvent;
import com.mccurry.game.boot.events.BootUiStateSetupCompleteEvent;
import com.mccurry.game.boot.events.AssetLoadingCompleteEvent;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class AssetLoader implements AsyncEventHandler<BootUiStateSetupCompleteEvent, GameOrchestrationContext> {
    private final EventBus<GameOrchestrationContext> orchestrationBus;
    private final SimpleApplication simpleApplication;

    @Override
    public void handle(@NonNull BootUiStateSetupCompleteEvent event, @NonNull GameOrchestrationContext eventContext, @Nullable EventLifecycleManager<BootUiStateSetupCompleteEvent> lifecycleManager) {
        System.out.println("[AssetLoader] Beginning to load assets");
        Thread.ofVirtual().start(
                () -> {
                    loadAssets(simpleApplication);
                    orchestrationBus.unsubscribe(BootUiStateSetupCompleteEvent.class, this);
                    System.out.println("[AssetLoader] Asset loading finished. ");
                    orchestrationBus.publish(
                            new AssetLoadingCompleteEvent(),
                            new GameOrchestrationContext(),
                            null
                    );

                    if(lifecycleManager != null){
                        lifecycleManager.cleanupEvent(event);
                    }
                }
        );
    }

    private void loadAssets(SimpleApplication simpleApplication){
        try {
            var allAssetsNum = 100;
            var libAssetsNum = 1;
            var assetCounter = new AtomicInteger();

            var lemurLibLoader = Thread.ofVirtual().start( () -> {
                GuiGlobals.initialize(simpleApplication);
                BaseStyles.loadGlassStyle();
                GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
                publishNextAssetLoadedEvent(allAssetsNum,assetCounter);
            });

            for (int i = 0; i < allAssetsNum-libAssetsNum; i++) {
                Thread.sleep(18,500_000);
                publishNextAssetLoadedEvent(allAssetsNum,assetCounter);
            }

            lemurLibLoader.join();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void publishNextAssetLoadedEvent(int allAssetsCount, AtomicInteger assetCounter){
        var assetsLoaded = (float) assetCounter.incrementAndGet();
        var normalizedPercent = assetsLoaded/allAssetsCount;
        System.out.println("Loading... %s%%".formatted(normalizedPercent));
        orchestrationBus.publish(
                new AssetLoadingPercentAdvanceEvent(normalizedPercent),
                new GameOrchestrationContext(),
                null
        );
    }
}
