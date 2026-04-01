package com.mccurry.game.util.event;

import com.mccurry.events.core.Event;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;


public class EventHandlers {

    public static <EVENT extends Event> SyncEventHandler<EVENT> syncEventHandler(
            BiConsumer<EVENT, EventLifecycleManager<EVENT>> consumer
    ){
        return new SyncEventHandler<EVENT>() {
            @Override
            public void handle(@NonNull EVENT event, @Nullable EventLifecycleManager<EVENT> lifecycleManager) {
                consumer.accept(event,lifecycleManager);
            }
        };
    }
}
