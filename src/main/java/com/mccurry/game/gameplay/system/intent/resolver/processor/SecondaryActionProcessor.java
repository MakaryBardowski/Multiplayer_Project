package com.mccurry.game.gameplay.system.intent.resolver.processor;

import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.AttackSystem;
import com.mccurry.game.gameplay.system.attack.component.WeaponIdentityComponent;
import com.mccurry.game.gameplay.system.equipment.core.component.EquipmentComponent;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;
import com.mccurry.game.gameplay.system.intent.event.PrimaryActionIntentEvent;
import com.mccurry.game.gameplay.system.intent.event.SecondaryActionIntentEvent;

public class SecondaryActionProcessor extends IntentProcessor<SecondaryActionIntentEvent> {
    private final AttackSystem attackSystem;

    public SecondaryActionProcessor(
            EntityStore entityStore,
            AttackSystem attackSystem
    ) {
        super(entityStore, SecondaryActionIntentEvent.class);
        this.attackSystem = attackSystem;
    }

    @Override
    public Class<SecondaryActionIntentEvent> getHandledIntentEvent() {
        return SecondaryActionIntentEvent.class;
    }

    @Override
    public void processIntentEvent(SecondaryActionIntentEvent event, float tpf, double gameTimeSeconds) {
        var intentOwner = event.getIntendingEntity();
        var equipmentComponent = entityStore.getComponent(intentOwner, EquipmentComponent.class);
        if (equipmentComponent != null) {

            var heldInRightHand = equipmentComponent.getWorn(HandSlot.HAND_RIGHT);

            if (heldInRightHand != null) {
                var weaponComponent = entityStore.getComponent(heldInRightHand, WeaponIdentityComponent.class);
                if (weaponComponent != null) {
                    attackSystem.processAttack(intentOwner, heldInRightHand, weaponComponent.getSecondaryActionIdentity(), tpf, gameTimeSeconds);
                }
            }
        }
    }
}
