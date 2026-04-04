package com.mccurry.game.gameplay.system.intent.resolver.processor;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.component.WeaponComponent;
import com.mccurry.game.gameplay.system.attack.event.AttackEvent;
import com.mccurry.game.gameplay.system.equipment.EquipmentComponent;
import com.mccurry.game.gameplay.system.equipment.model.HandSlot;
import com.mccurry.game.gameplay.system.intent.model.Intent;

public class PrimaryActionProcessor extends IntentProcessor {


    public PrimaryActionProcessor(EventBus eventBus, EntityStore entityStore) {
        super(eventBus, entityStore);
    }

    @Override
    public Intent getHandledIntent() {
        return Intent.PRIMARY_ACTION;
    }

    @Override
    public void processIntent(Entity intentOwner) {
        var equipmentComponent = entityStore.getComponent(intentOwner, EquipmentComponent.class);
        if (equipmentComponent != null) {

            var rightHandTool = equipmentComponent.getTool(HandSlot.HAND_RIGHT);

            if (rightHandTool != null) {
                var weaponComponent = entityStore.getComponent(rightHandTool, WeaponComponent.class);
                if (weaponComponent != null && weaponComponent.getCooldown() <= 0) {
                    eventBus.publish(new AttackEvent(intentOwner, rightHandTool), null);
                    System.err.println("Entity %s attacks with weapon %s - dealing: %s damage. It will be available for use in %s seconds!".formatted(
                                    intentOwner.getId(),
                                    rightHandTool.getId(),
                                    weaponComponent.getBaseDamage(),
                                    weaponComponent.getCooldown()
                            )
                    );
                }
            }
        }
    }
}
