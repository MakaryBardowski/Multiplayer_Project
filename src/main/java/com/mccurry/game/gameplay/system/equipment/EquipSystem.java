package com.mccurry.game.gameplay.system.equipment;

import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.equipment.model.BodySlot;
import com.mccurry.game.gameplay.system.equipment.model.HandSlot;
import lombok.RequiredArgsConstructor;

/**
 * Manages equipping items by entities
 */
@RequiredArgsConstructor
public class EquipSystem {
    private final EntityStore entityStore;

    /**
     * @param handSlot slot to equip on
     * @param owner entity having EquipmentComponent (target)
     * @param equipped the entity to equip in the hand
     */
    public void equipHand(HandSlot handSlot, Entity owner, Entity equipped){

    }


    /**
     * @param bodySlot slot to equip on
     * @param owner entity having EquipmentComponent (target)
     * @param equipped the entity to equip in the hand
     */
    public void equipBody(BodySlot bodySlot, Entity owner, Entity equipped){

    }
}
