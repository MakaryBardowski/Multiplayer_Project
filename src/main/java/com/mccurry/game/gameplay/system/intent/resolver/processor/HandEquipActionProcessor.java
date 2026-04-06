package com.mccurry.game.gameplay.system.intent.resolver.processor;

import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.equipment.core.EquipSystem;
import com.mccurry.game.gameplay.system.intent.event.HandEquipIntentEvent;

public class HandEquipActionProcessor extends IntentProcessor<HandEquipIntentEvent> {

    private final EquipSystem equipSystem;

    public HandEquipActionProcessor(
            EntityStore entityStore,
            EquipSystem equipSystem
    ) {
        super(entityStore, HandEquipIntentEvent.class);
        this.equipSystem = equipSystem;
    }

    @Override
    public Class<HandEquipIntentEvent> getHandledIntentEvent() {
        return HandEquipIntentEvent.class;
    }

    @Override
    public void processIntentEvent(HandEquipIntentEvent event, double gameTimeSeconds) {
        equipSystem.equipHand(event.getSlot(), event.getIntendingEntity(), event.getNotValidatedCandidate());
    }
}
