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
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.util.event.EventHandlers;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the first person model attachment when equipping items by entities
 */
public class FirstPersonVisualEquipSystem implements EventSubscriber {
    private final EntityStore entityStore;

    private final Node firstPersonViewNode = new Node("First Person Node");

    private final SyncEventHandler<HandEquippedEvent> handEquippedEventHandler;

    public FirstPersonVisualEquipSystem(EntityStore entityStore){
        this.entityStore = entityStore;
        this.handEquippedEventHandler = EventHandlers.syncEventHandler(
                (handEquippedEvent, lifecycleManager) -> {
                    var owner = handEquippedEvent.getOwner();
                    var item = handEquippedEvent.getItemInHands();
                    var slot = handEquippedEvent.getTargetSlot();


                    tryAttachOnHumanoid(slot,owner, item);
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

    private void tryAttachOnHumanoid(HandSlot handSlot, Entity owner, Entity equipped){
        var cameraHolderComponent = entityStore.getComponent(owner, CameraHolderComponent.class);
        if(cameraHolderComponent == null){
            return;
        }

        var humanoidModel = entityStore.getComponent(owner, HumanoidModelComponent.class);
        if(humanoidModel == null){
            return;
        }

        var equippableComponent = entityStore.getComponent(equipped, EquippableComponent.class);


    }
}
