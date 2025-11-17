package com.mccurry.game.shared.util.event;

import com.mccurry.events.core.Event;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventContext;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import lombok.NonNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class EventSubscribers {
    public static <EVENT extends Event,CTX extends EventContext> EventSubscriber<CTX> syncUnsubscribingOnFirstMessage(
            Class<EVENT> handledEventClass,
            EventBus<CTX> bus,
            Consumer<EVENT> eventConsumer
    ){
        return new EventSubscriber<>() {
            private final SyncEventHandler<EVENT, CTX> handler = (event, ctx, lifecycleManager) -> {
                eventConsumer.accept(event);
                unsubscribe(bus);
            };

            @Override
            public void subscribe(@NonNull EventBus<CTX> eventBus) {
                eventBus.subscribe(handledEventClass, handler);
            }

            @Override
            public void unsubscribe(@NonNull EventBus<CTX> eventBus) {
                eventBus.unsubscribe(handledEventClass, handler);
            }
        };
    }


    public static <EVENT extends Event,CTX extends EventContext> EventSubscriber<CTX> syncUnsubscribingOnCondition(
            Class<EVENT> handledEventClass,
            EventBus<CTX> bus,
            Consumer<EVENT> eventConsumer,
            Predicate<EVENT> unsubscribeCondition
    ){
        return new EventSubscriber<>() {
            private final SyncEventHandler<EVENT, CTX> handler = (event, ctx, lifecycleManager) -> {
                eventConsumer.accept(event);
                if(unsubscribeCondition.test(event)) {
                    unsubscribe(bus);
                }
            };

            @Override
            public void subscribe(@NonNull EventBus<CTX> eventBus) {
                eventBus.subscribe(handledEventClass, handler);
            }

            @Override
            public void unsubscribe(@NonNull EventBus<CTX> eventBus) {
                eventBus.unsubscribe(handledEventClass, handler);
            }
        };
    }


    public static <EVENT extends Event,CTX extends EventContext> EventSubscriber<CTX> syncAlwaysSubscribed(
            Class<EVENT> handledEventClass,
            EventBus<CTX> bus,
            Consumer<EVENT> eventConsumer
    ){
        return new EventSubscriber<>() {
            private final SyncEventHandler<EVENT, CTX> handler = (event, ctx, lifecycleManager) -> {
                eventConsumer.accept(event);
            };

            @Override
            public void subscribe(@NonNull EventBus<CTX> eventBus) {
                eventBus.subscribe(handledEventClass, handler);
            }

            @Override
            public void unsubscribe(@NonNull EventBus<CTX> eventBus) {}
        };
    }
}
