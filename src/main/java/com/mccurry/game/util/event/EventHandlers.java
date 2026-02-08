package com.mccurry.game.util.event;

import com.mccurry.events.core.Event;
import com.mccurry.events.core.EventContext;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import dirty.Utils.TriConsumer;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;


public class EventHandlers {

    public static <EVENT extends Event,CTX extends EventContext> SyncEventHandler<EVENT,CTX> syncEventHandler(
            TriConsumer<EVENT,CTX, EventLifecycleManager<EVENT>> consumer
    ){
        return new SyncEventHandler<EVENT, CTX>() {
            @Override
            public void handle(@NonNull EVENT event, @NonNull CTX eventContext, @Nullable EventLifecycleManager<EVENT> lifecycleManager) {
                consumer.accept(event,eventContext,lifecycleManager);
            }
        };
    }
}
