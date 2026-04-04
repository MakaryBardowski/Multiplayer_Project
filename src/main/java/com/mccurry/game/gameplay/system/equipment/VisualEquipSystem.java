package com.mccurry.game.gameplay.system.equipment;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.game.gameplay.system.equipment.event.EquipWeaponEvent;
import lombok.NonNull;

/**
 * Manages the model attachment when equipping items by entities
 */
public class VisualEquipSystem implements EventSubscriber {

    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(EquipWeaponEvent.class, null);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(EquipWeaponEvent.class, null);
    }
}
