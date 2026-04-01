package com.mccurry.game.gameplay.system.intent.system;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameplay.system.intent.resolver.IntentResolver;
import com.mccurry.game.gameplay.system.intent.event.IntentEvent;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class IntentSystem implements SyncEventHandler<IntentEvent, GameOrchestrationContext>, EventSubscriber<GameOrchestrationContext> {

    private final IntentResolver intentResolver = new IntentResolver();

    @Override
    public void handle(@NonNull IntentEvent event, @NonNull GameOrchestrationContext eventContext, @Nullable EventLifecycleManager<IntentEvent> lifecycleManager) {
        intentResolver.resolve(event.getIntent(), event.getTargetEntity());
    }

    @Override
    public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(IntentEvent.class, this);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(IntentEvent.class, this);
    }
}
