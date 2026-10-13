package com.mccurry.game.gameplay.system.intent.resolver.processor;

import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.core.AttackSystem;
import com.mccurry.game.gameplay.system.attack.core.component.WeaponIdentityComponent;
import com.mccurry.game.gameplay.system.equipment.core.component.EquipmentComponent;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;
import com.mccurry.game.gameplay.system.intent.event.PrimaryActionIntentEvent;

import static com.mccurry.game.gameplay.system.attack.core.model.AttackAction.PRIMARY;

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
    public void processIntentEvent(PrimaryActionIntentEvent event, float tpf, double gameTimeSeconds) {
        var intentOwner = event.getIntendingEntity();
        var equipmentComponent = entityStore.getComponent(intentOwner, EquipmentComponent.class);
        if (equipmentComponent != null) {

            var heldInRightHand = equipmentComponent.getWorn(HandSlot.HAND_RIGHT);

            if (heldInRightHand != null) {
                var weaponComponent = entityStore.getComponent(heldInRightHand, WeaponIdentityComponent.class);

                if (weaponComponent != null) {
                    attackSystem.processAttack(intentOwner, heldInRightHand, weaponComponent, PRIMARY, tpf, gameTimeSeconds);
                }
            }
        }
    }
}
