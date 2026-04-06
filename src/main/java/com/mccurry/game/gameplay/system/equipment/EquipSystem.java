package com.mccurry.game.gameplay.system.equipment;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.equipment.component.EquipmentComponent;
import com.mccurry.game.gameplay.system.equipment.component.EquippableComponent;
import com.mccurry.game.gameplay.system.equipment.event.HandEquippedEvent;
import com.mccurry.game.gameplay.system.equipment.model.BodySlot;
import com.mccurry.game.gameplay.system.equipment.model.HandSlot;
import lombok.RequiredArgsConstructor;

/**
 * Manages equipping items by entities
 */
@RequiredArgsConstructor
public class EquipSystem {
    private final EventBus gameEventBus;
    private final EntityStore entityStore;

    /**
     * @param handSlot slot to equip on
     * @param owner entity having EquipmentComponent (target)
     * @param equipped the entity to equip in the hand (unvalidated)
     */
    public void equipHand(HandSlot handSlot, Entity owner, Entity equipped){

        var ownerEquipmentComponent = entityStore.getComponent(owner, EquipmentComponent.class);
        if (ownerEquipmentComponent == null) {
            return;
        }

        var equippableComponent = entityStore.getComponent(equipped, EquippableComponent.class);
        if(equippableComponent == null){
            return;
        }

        if(equippableComponent.getHandSlots().contains(handSlot)){
            ownerEquipmentComponent.setWorn(handSlot, equipped);
            gameEventBus.publish(
                    new HandEquippedEvent(owner, equipped, handSlot),
                    null
            );
        }
    }


    /**
     * @param bodySlot slot to equip on
     * @param owner entity having EquipmentComponent (target)
     * @param equipped the entity to equip on the body (unvalidated)
     */
    public void equipBody(BodySlot bodySlot, Entity owner, Entity equipped){

    }
}
