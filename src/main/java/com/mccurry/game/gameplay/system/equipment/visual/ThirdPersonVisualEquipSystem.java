package com.mccurry.game.gameplay.system.equipment.visual;

import com.jme3.scene.Node;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.equipment.core.component.EquippableComponent;
import com.mccurry.game.gameplay.system.equipment.core.event.HandEquippedEvent;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;
import com.mccurry.game.gameplay.system.modelhierarchy.component.HumanoidModelComponent;
import com.mccurry.game.util.event.EventHandlers;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the third person model attachment when equipping items by entities
 */
public class ThirdPersonVisualEquipSystem implements EventSubscriber {

    private final EntityStore entityStore;
    private final SyncEventHandler<HandEquippedEvent> handEquippedEventHandler;


    public ThirdPersonVisualEquipSystem(EntityStore entityStore){
        this.entityStore = entityStore;
        this.handEquippedEventHandler = EventHandlers.syncEventHandler(
                (handEquippedEvent, lifecycleManager) -> {
                    var owner = handEquippedEvent.getOwner();
                    var item = handEquippedEvent.getItemInHands();
                    var slot = handEquippedEvent.getTargetSlot();

                    var equippableComponent = entityStore.getComponent(item, EquippableComponent.class);

                    var displayNode = equippableComponent.getDisplayNode();
                    if(displayNode == null){
                        return;
                    }

                    tryAttachOnHumanoid(slot,owner, displayNode);
                }
        );
    }

    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(HandEquippedEvent.class, handEquippedEventHandler);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(HandEquippedEvent.class, handEquippedEventHandler);
    }

    private void tryAttachOnHumanoid(HandSlot handSlot, Entity owner, @NotNull Node displayNode){
        var humanoidModel = entityStore.getComponent(owner, HumanoidModelComponent.class);
        if(humanoidModel == null){
            return;
        }

        if(handSlot.equals(HandSlot.HAND_RIGHT)) {
            var rightHand = humanoidModel.getRightHandAttachmentNode();
            rightHand.attachChild(displayNode);

        } else if (handSlot.equals(HandSlot.HAND_LEFT)){
            var leftHand = humanoidModel.getLeftHandAttachmentNode();
            leftHand.attachChild(displayNode);
        }
    }
}
