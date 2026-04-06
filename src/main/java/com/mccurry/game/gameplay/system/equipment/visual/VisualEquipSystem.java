package com.mccurry.game.gameplay.system.equipment.visual;

import com.jme3.asset.AssetManager;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.equipment.core.component.EquippableComponent;
import com.mccurry.game.gameplay.system.equipment.core.event.HandEquippedEvent;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;
import com.mccurry.game.gameplay.system.modelhierarchy.component.HumanoidModelComponent;
import com.mccurry.game.util.event.EventHandlers;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Manages the model attachment when equipping items by entities
 */
public class VisualEquipSystem implements EventSubscriber {

    private final EntityStore entityStore;
    private final SyncEventHandler<HandEquippedEvent> handEquippedEventHandler;


    public VisualEquipSystem(EntityStore entityStore){
        this.entityStore = entityStore;
        this.handEquippedEventHandler = EventHandlers.syncEventHandler(
                (handEquippedEvent, lifecycleManager) -> {
                    var owner = handEquippedEvent.getOwner();
                    var item = handEquippedEvent.getItemInHands();
                    var slot = handEquippedEvent.getTargetSlot();

                    var humanoidModel = entityStore.getComponent(owner, HumanoidModelComponent.class);
                    if(humanoidModel == null){
                        return;
                    }

                    var equippableComponent = entityStore.getComponent(item, EquippableComponent.class);

                    var displayNode = equippableComponent.getDisplayNode();
                    if(displayNode == null){
                        return;
                    }

                    if(slot.equals(HandSlot.HAND_RIGHT)) {
                        var rightHand = humanoidModel.getRightHandAttachmentNode();
                        rightHand.attachChild(displayNode);

                    } else if (slot.equals(HandSlot.HAND_LEFT)){
                        var leftHand = humanoidModel.getLeftHandAttachmentNode();
                        leftHand.attachChild(displayNode);
                    }
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
}
