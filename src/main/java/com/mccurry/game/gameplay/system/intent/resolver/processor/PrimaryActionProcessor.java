package com.mccurry.game.gameplay.system.intent.resolver.processor;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.AttackSystem;
import com.mccurry.game.gameplay.system.attack.component.WeaponComponent;
import com.mccurry.game.gameplay.system.equipment.component.EquipmentComponent;
import com.mccurry.game.gameplay.system.equipment.model.HandSlot;
import com.mccurry.game.gameplay.system.intent.model.Intent;

public class PrimaryActionProcessor extends IntentProcessor {
    private final AttackSystem attackSystem;

    public PrimaryActionProcessor(
            EventBus eventBus,
            EntityStore entityStore,
            AttackSystem attackSystem
    ) {
        super(eventBus, entityStore);
        this.attackSystem = attackSystem;
    }

    @Override
    public Intent getHandledIntent() {
        return Intent.PRIMARY_ACTION;
    }

    @Override
    public void processIntent(Entity intentOwner, double gameTimeSeconds) {
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
