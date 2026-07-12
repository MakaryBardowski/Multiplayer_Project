package com.mccurry.game.gameplay.system.equipment.visual.firstperson;

import com.jme3.scene.Node;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.WeaponViewModelComponent;
import com.mccurry.game.gameplay.system.crosshair.component.CrosshairSourceComponent;
import com.mccurry.game.gameplay.system.crosshair.event.CrosshairChangeRequestEvent;
import com.mccurry.game.gameplay.system.equipment.core.event.HandEquippedEvent;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.HandsViewmodelComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import com.mccurry.game.util.event.EventHandlers;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the first person model attachment when equipping items by entities
 */
public class FirstPersonEquipSystem implements EventSubscriber {
    // todo: must listen to RefreshFirstPersonView
    private final EntityStore entityStore;
    private final EventBus eventBus;
    private final SyncEventHandler<HandEquippedEvent> handEquippedEventHandler;


    public FirstPersonEquipSystem(
            EntityStore entityStore,
            EventBus eventBus
    ){
        this.entityStore = entityStore;
        this.eventBus = eventBus;
        this.handEquippedEventHandler = EventHandlers.syncEventHandler(
                (handEquippedEvent, lifecycleManager) -> {
                    var owner = handEquippedEvent.getOwner();
                    var item = handEquippedEvent.getItemInHands();
                    var slot = handEquippedEvent.getTargetSlot();

                    tryAttach(slot,owner, item);
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

    private void tryAttach(HandSlot handSlot, Entity owner, Entity equipped){
        var cameraHolderComponent = entityStore.getComponent(owner, CameraHolderComponent.class);
        if(cameraHolderComponent == null){
            return;
        }

        var transform = entityStore.getComponent(owner, TransformComponent.class);

        if(transform == null){
            return;
        }

        // assert equipmentComponent != null
        var handsViewmodel = entityStore.getComponent(owner, HandsViewmodelComponent.class);
        if(handsViewmodel != null) {

            var attachmentNode = switch (handSlot){
                case HAND_RIGHT -> handsViewmodel.getRightHandAttachmentNode();
                case HAND_LEFT -> handsViewmodel.getLeftHandAttachmentNode();
            };
            attachAt(attachmentNode,equipped);
        }

        var crosshairSource = entityStore.getComponent(equipped, CrosshairSourceComponent.class);
        if(crosshairSource != null){
            eventBus.publish(new CrosshairChangeRequestEvent(crosshairSource.getCrosshairType()), null);
        }

        System.out.println("attached first person");
    }


    private void attachAt(@NotNull Node attachmentNode, Entity equippedItem){
        attachmentNode.detachAllChildren();
        var gunViewmodel = entityStore.getComponent(equippedItem, WeaponViewModelComponent.class);

        if(gunViewmodel != null){
            var viewmodel = gunViewmodel.getViewModel();
            if(viewmodel != null){
                attachmentNode.attachChild(viewmodel);
            }
        }
    }
}
