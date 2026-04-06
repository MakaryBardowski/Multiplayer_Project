package com.mccurry.game.gameplay.system.intent.resolver.processor;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.AttackSystem;
import com.mccurry.game.gameplay.system.attack.component.WeaponComponent;
import com.mccurry.game.gameplay.system.equipment.component.EquipmentComponent;
import com.mccurry.game.gameplay.system.equipment.model.HandSlot;
import com.mccurry.game.gameplay.system.intent.event.PrimaryActionIntentEvent;

public class PrimaryActionProcessor extends IntentProcessor<PrimaryActionIntentEvent> {
    private final AttackSystem attackSystem;

    public PrimaryActionProcessor(
            EntityStore entityStore,
            AttackSystem attackSystem
    ) {
        super(entityStore, PrimaryActionIntentEvent.class);
        this.attackSystem = attackSystem;
    }

    @Override
    public Class<PrimaryActionIntentEvent> getHandledIntentEvent() {
        return PrimaryActionIntentEvent.class;
    }

    @Override
    public void processIntentEvent(PrimaryActionIntentEvent event, double gameTimeSeconds) {
        var intentOwner = event.getIntendingEntity();
        var equipmentComponent = entityStore.getComponent(intentOwner, EquipmentComponent.class);
        if (equipmentComponent != null) {

            var heldInRightHand = equipmentComponent.getWorn(HandSlot.HAND_RIGHT);

            if (heldInRightHand != null) {
                var weaponComponent = entityStore.getComponent(heldInRightHand, WeaponComponent.class);
                if (weaponComponent != null) {
                    attackSystem.processAttack(intentOwner, heldInRightHand, weaponComponent, gameTimeSeconds);
                }
            }
        }
    }
}
