package com.mccurry.game.gameplay.system.intent.resolver.processor;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.intent.event.IntentEvent;

public abstract class IntentProcessor<T extends IntentEvent> {
    protected final EntityStore entityStore;
    private final Class<T> processedEventType;

    public IntentProcessor(EntityStore entityStore, Class<T> intentEventClass){
        this.entityStore = entityStore;
        this.processedEventType = intentEventClass;
    }

    public abstract Class<T> getHandledIntentEvent();
    protected abstract void processIntentEvent(T intentEvent , float tpf, double gameTimeSeconds);
    public void processIntent(IntentEvent intentEvent, float tpf, double gameTimeSeconds){
        processIntentEvent(processedEventType.cast(intentEvent), tpf, gameTimeSeconds);
    }
}
