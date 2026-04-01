package com.mccurry.game.util.event;

import com.mccurry.events.core.Event;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import lombok.NonNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventSubscribers {
    public static <EVENT extends Event> EventSubscriber syncUnsubscribingOnFirstMessage(
            Class<EVENT> handledEventClass,
            EventBus bus,
            Consumer<EVENT> eventConsumer
    ){
        return new EventSubscriber() {
            private final SyncEventHandler<EVENT> handler = (event, lifecycleManager) -> {
                eventConsumer.accept(event);
                unsubscribe(bus);
            };

            @Override
            public void subscribe(@NonNull EventBus eventBus) {
                eventBus.subscribe(handledEventClass, handler);
            }

            @Override
            public void unsubscribe(@NonNull EventBus eventBus) {
                eventBus.unsubscribe(handledEventClass, handler);
            }
        };
    }


    public static <EVENT extends Event> EventSubscriber syncUnsubscribingOnCondition(
            Class<EVENT> handledEventClass,
            EventBus bus,
            Consumer<EVENT> eventConsumer,
            Predicate<EVENT> unsubscribeCondition
    ){
        return new EventSubscriber() {
            private final SyncEventHandler<EVENT> handler = (event, lifecycleManager) -> {
                eventConsumer.accept(event);
                if(unsubscribeCondition.test(event)) {
                    unsubscribe(bus);
                }
            };

            @Override
            public void subscribe(@NonNull EventBus eventBus) {
                eventBus.subscribe(handledEventClass, handler);
            }

            @Override
            public void unsubscribe(@NonNull EventBus eventBus) {
                eventBus.unsubscribe(handledEventClass, handler);
            }
        };
    }


    public static <EVENT extends Event> EventSubscriber syncAlwaysSubscribed(
            Class<EVENT> handledEventClass,
            EventBus bus,
            Consumer<EVENT> eventConsumer
    ){
        return new EventSubscriber() {
            private final SyncEventHandler<EVENT> handler = (event, lifecycleManager) -> {
                eventConsumer.accept(event);
            };

            @Override
            public void subscribe(@NonNull EventBus eventBus) {
                eventBus.subscribe(handledEventClass, handler);
            }

            @Override
            public void unsubscribe(@NonNull EventBus eventBus) {}
        };
    }
}
