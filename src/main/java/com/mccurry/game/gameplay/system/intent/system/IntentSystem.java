package com.mccurry.game.gameplay.system.intent.system;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.QueueSystem;
import com.mccurry.game.gameplay.system.intent.resolver.IntentResolver;
import com.mccurry.game.gameplay.system.intent.event.IntentEvent;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IntentSystem implements QueueSystem,SyncEventHandler<IntentEvent>, EventSubscriber {

    private final IntentResolver intentResolver;
    private final List<IntentEvent> intentEventQueue = new ArrayList<>();

    public IntentSystem(EventBus eventBus, EntityStore entityStore){
        intentResolver = new IntentResolver(eventBus, entityStore);
    }

    public void processQueued(float tpf, double gameTimeSeconds){
        var iterator = intentEventQueue.iterator();
        while(iterator.hasNext()){
            var event = iterator.next();
            iterator.remove();
            intentResolver.resolve(event.getIntent(), event.getTargetEntity(), gameTimeSeconds);
        }
    }

    @Override
    public synchronized void handle(@NonNull IntentEvent event, @Nullable EventLifecycleManager<IntentEvent> lifecycleManager) {
        intentEventQueue.add(event);
    }

    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(IntentEvent.class, this);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(IntentEvent.class, this);
    }
}
