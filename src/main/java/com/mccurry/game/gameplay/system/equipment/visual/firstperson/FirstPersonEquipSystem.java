package com.mccurry.game.gameplay.system.equipment.visual.firstperson;

import com.jme3.scene.Node;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.core.component.WeaponIdentityComponent;
import com.mccurry.game.gameplay.system.attack.core.component.viewmodel.WeaponViewModelComponent;
import com.mccurry.game.gameplay.system.crosshair.component.CrosshairSourceComponent;
import com.mccurry.game.gameplay.system.crosshair.event.CrosshairChangeRequestEvent;
import com.mccurry.game.gameplay.system.crosshair.model.CrosshairType;
import com.mccurry.game.gameplay.system.equipment.core.component.EquipmentComponent;
import com.mccurry.game.gameplay.system.equipment.core.event.HandEquippedEvent;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.HandsViewmodelComponent;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.event.RefreshFirstPersonViewEvent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import com.mccurry.game.util.event.EventHandlers;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the first person model attachment when equipping items by entities
 */
public class FirstPersonEquipSystem implements EventSubscriber {
    private final EntityStore entityStore;
    private final EventBus eventBus;
    private final SyncEventHandler<HandEquippedEvent> handEquippedEventHandler;
    private final SyncEventHandler<RefreshFirstPersonViewEvent> refreshViewHandler;


    public FirstPersonEquipSystem(
            EntityStore entityStore,
            EventBus eventBus,
            Node fpHandsParentNode
    ) {
        this.entityStore = entityStore;
        this.eventBus = eventBus;
        this.handEquippedEventHandler = EventHandlers.syncEventHandler(
                (handEquippedEvent, lifecycleManager) -> {
                    var owner = handEquippedEvent.getOwner();
                    var item = handEquippedEvent.getItemInHands();
                    var slot = handEquippedEvent.getTargetSlot();

                    attachIfCameraHolder(slot, owner, item);
                }
        );

        this.refreshViewHandler = EventHandlers.syncEventHandler((refreshEvent, lifecycleManager) -> {
            fpHandsParentNode.detachAllChildren();

            eventBus.publish(
                    new CrosshairChangeRequestEvent(
                            CrosshairType.DEFAULT,
                            0,
                            0,
                            0
                    ),
                    null
            );

            var holder = entityStore.getSingletonComponentHolder(CameraHolderComponent.class);

            if (holder == null) {
                return;
            }

            var handsViewmodel = entityStore.getComponent(holder, HandsViewmodelComponent.class);

            if(handsViewmodel == null){
                return;
            }

            fpHandsParentNode.attachChild(handsViewmodel.getRootHandsNode());

            var equipment = entityStore.getComponent(holder, EquipmentComponent.class);

            if (equipment == null) {
                return;
            }

            for (var handSlot : HandSlot.values()) {
                var equippedItem = equipment.getWorn(handSlot);
                if(equippedItem == null){
                    continue;
                }
                tryAttach(handSlot, holder, equippedItem);
            }

        });
    }

    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(HandEquippedEvent.class, handEquippedEventHandler);
        eventBus.subscribe(RefreshFirstPersonViewEvent.class, refreshViewHandler);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(HandEquippedEvent.class, handEquippedEventHandler);
        eventBus.unsubscribe(RefreshFirstPersonViewEvent.class, refreshViewHandler);
    }

    private void attachIfCameraHolder(@NotNull HandSlot handSlot, @NotNull Entity owner, @NotNull Entity equipped) {
        var cameraHolder = entityStore.getSingletonComponentHolder(CameraHolderComponent.class);
        if (cameraHolder == null || !cameraHolder.getId().equals(owner.getId())) {
            return;
        }

        tryAttach(handSlot, owner, equipped);
    }

    private void tryAttach(@NotNull HandSlot handSlot, @NotNull Entity cameraHolder, @NotNull Entity equipped) {

        var transform = entityStore.getComponent(cameraHolder, TransformComponent.class);

        if (transform == null) {
            return;
        }

        // assert equipmentComponent != null
        var handsViewmodel = entityStore.getComponent(cameraHolder, HandsViewmodelComponent.class);
        if (handsViewmodel != null) {

            var attachmentNode = switch (handSlot) {
                case HAND_RIGHT -> handsViewmodel.getRightHandAttachmentNode();
                case HAND_LEFT -> handsViewmodel.getLeftHandAttachmentNode();
            };
            attachAt(attachmentNode, equipped);
        }

        var crosshairSource = entityStore.getComponent(equipped, CrosshairSourceComponent.class);
        if (crosshairSource != null) {
            float angleChangePerSec = 0;
            float minAngle = 0;
            float maxAngle = 0;

            var weaponIdentityComponent = entityStore.getComponent(equipped, WeaponIdentityComponent.class);
            if (weaponIdentityComponent != null) {
                var spreadData = weaponIdentityComponent.getSpreadData();

                if (spreadData != null) {
                    minAngle = spreadData.getMinimumSpread().getAngle();
                    maxAngle = spreadData.getMaximumSpread().getAngle();

                    var heat = spreadData.getHeat();
                    angleChangePerSec = heat.getLevelChangeRatePerSecond() * ((maxAngle - minAngle) / heat.getMaxLevel());
                }
            }

            eventBus.publish(
                    new CrosshairChangeRequestEvent(
                            crosshairSource.getCrosshairType(),
                            angleChangePerSec,
                            minAngle,
                            maxAngle
                    ),
                    null
            );
        }

        System.out.println("attached first person");
    }

    private void attachAt(@NotNull Node attachmentNode, Entity equippedItem) {
        attachmentNode.detachAllChildren();
        var gunViewmodel = entityStore.getComponent(equippedItem, WeaponViewModelComponent.class);

        if (gunViewmodel != null) {
            var viewmodel = gunViewmodel.getViewModel();
            if (viewmodel != null) {
                attachmentNode.attachChild(viewmodel);
            }
        }
    }


}
