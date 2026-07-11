package com.mccurry.game.gameplay.system.intent.system;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.QueueSystem;
import com.mccurry.game.gameplay.system.intent.event.HandEquipIntentEvent;
import com.mccurry.game.gameplay.system.intent.event.PrimaryActionIntentEvent;
import com.mccurry.game.gameplay.system.intent.event.SecondaryActionIntentEvent;
import com.mccurry.game.gameplay.system.intent.resolver.IntentResolver;
import com.mccurry.game.gameplay.system.intent.event.IntentEvent;
import com.mccurry.game.gameplay.system.intent.resolver.model.IntentSystems;
import com.mccurry.game.util.event.EventHandlers;
import lombok.NonNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class IntentSystem implements QueueSystem, EventSubscriber {

    private final IntentResolver intentResolver;
    private final List<IntentEvent> intentEventQueue = new ArrayList<>();
    private final Map<Class<? extends IntentEvent>, SyncEventHandler<? extends IntentEvent> > intentHandlersByEventClass = Map.ofEntries(
            genericIntentListenerEntry(PrimaryActionIntentEvent.class, intentEventListener(PrimaryActionIntentEvent.class)),
            genericIntentListenerEntry(SecondaryActionIntentEvent.class, intentEventListener(SecondaryActionIntentEvent.class)),
            genericIntentListenerEntry(HandEquipIntentEvent.class, intentEventListener(HandEquipIntentEvent.class))
    );

    public IntentSystem(IntentSystems intentSystems, EntityStore entityStore){
        intentResolver = new IntentResolver(intentSystems, entityStore);
    }

    public void processQueued(float tpf, double gameTimeSeconds){
        var iterator = intentEventQueue.iterator();
        while(iterator.hasNext()){
            var event = iterator.next();
            iterator.remove();
            intentResolver.resolve(event, tpf, gameTimeSeconds);
        }
    }

    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        for(var entry : intentHandlersByEventClass.entrySet()){
            // this is guaranteed to be correct because of the map initialization, i didnt change it because my event lib sucks:P
            eventBus.subscribe(entry.getKey(), (SyncEventHandler) entry.getValue());
        }
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        for(var entry : intentHandlersByEventClass.entrySet()){
            // this is guaranteed to be correct because of the map initialization, i didnt change it because my event lib sucks:P
            eventBus.unsubscribe(entry.getKey(), (SyncEventHandler) entry.getValue());
        }
    }


    private <T extends IntentEvent> SyncEventHandler<T> intentEventListener(
            @NonNull Class<T> eventClass
    ) {
        BiConsumer<T, EventLifecycleManager<T>> consumer = (event, lifecycle) -> {
           intentEventQueue.add(event);
        };

        return EventHandlers.synchronizedSyncEventHandler(consumer);
    }

    private static <T extends IntentEvent> Map.Entry<Class<T>, SyncEventHandler<T>> genericIntentListenerEntry(
            Class<T> clazz, SyncEventHandler<T> handler
    ) {
        return new AbstractMap.SimpleEntry<>(clazz, handler);
    }
}
