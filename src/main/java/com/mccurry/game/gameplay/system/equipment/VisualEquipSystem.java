package com.mccurry.game.gameplay.system.equipment;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameplay.system.equipment.event.HandEquippedEvent;
import com.mccurry.game.util.event.EventHandlers;
import lombok.NonNull;

/**
 * Manages the model attachment when equipping items by entities
 */
public class VisualEquipSystem implements EventSubscriber {

    private SyncEventHandler<HandEquippedEvent> handEquippedEventHandler = EventHandlers.syncEventHandler(
            (handEquippedEvent, lifecycleManager) -> {
                var owner = handEquippedEvent.getOwner();
                var item = handEquippedEvent.getItemInHands();
                var slot = handEquippedEvent.getTargetSlot();
            }
    );

    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(HandEquippedEvent.class, handEquippedEventHandler);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(HandEquippedEvent.class, handEquippedEventHandler);
    }
}
