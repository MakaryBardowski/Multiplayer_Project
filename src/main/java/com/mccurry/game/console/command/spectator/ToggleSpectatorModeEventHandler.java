package com.mccurry.game.console.command.spectator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.console.event.ToggleSpectatorModeEvent;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.event.RefreshFirstPersonViewEvent;
import com.mccurry.game.gameplay.system.movement.component.VelocityComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.player.component.control.PlayerControlledComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class ToggleSpectatorModeEventHandler implements SyncEventHandler<ToggleSpectatorModeEvent> {
    private final EntityStore entityStore;
    private final EventBus eventBus;

    private Entity originalCameraHolder;
    private CameraHolderComponent originalCamComponent;

    public ToggleSpectatorModeEventHandler(EventBus eventBus, EntityStore entityStore) {
        this.entityStore = entityStore;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(@NonNull ToggleSpectatorModeEvent event, @Nullable EventLifecycleManager<ToggleSpectatorModeEvent> lifecycleManager) {
        if (originalCameraHolder != null) {

            var newCamHolderComp = new CameraHolderComponent(originalCamComponent.getCurrentActualCameraOffset());
            newCamHolderComp.getCurrentDesiredCameraRotation().set(originalCamComponent.getCurrentDesiredCameraRotation());

            entityStore.addComponentsToEntity(originalCameraHolder, newCamHolderComp, new PlayerControlledComponent());
            eventBus.publish(new RefreshFirstPersonViewEvent(), null);

            originalCameraHolder = null;
            originalCamComponent = null;
            return;
        }

        originalCameraHolder = entityStore.getSingletonComponentHolder(CameraHolderComponent.class);
        originalCamComponent = entityStore.getComponent(originalCameraHolder, CameraHolderComponent.class);

        if(originalCameraHolder == null){
            return;
        }

        var originalTransform = entityStore.getComponent(originalCameraHolder, TransformComponent.class);

        var originalPos = new Vector3f(0, 3, 0);
        var originalNodeRot = Quaternion.IDENTITY.clone();
        if (originalTransform != null) {
            originalPos.set(originalTransform.getOriginNode().getWorldTranslation());
            originalNodeRot.set(originalTransform.getVisualNode().getLocalRotation());
        }

        var newCamHolderComp = new CameraHolderComponent(originalCamComponent.getCurrentActualCameraOffset());
        newCamHolderComp.getCurrentDesiredCameraRotation().set(originalCamComponent.getCurrentDesiredCameraRotation());

        var transformComponent = new TransformComponent();

        entityStore.create(
                newCamHolderComp,
                new PlayerControlledComponent(),
                transformComponent,
                new VelocityComponent()
        );
        transformComponent.getOriginNode().move(originalPos);
        transformComponent.getVisualNode().setLocalRotation(originalNodeRot);

        var originalVelocity = entityStore.getComponent(originalCameraHolder, VelocityComponent.class);
        if(originalVelocity == null){
            return;
        }
        originalVelocity.getDesiredVelocity().set(0,0,0);


        eventBus.publish(new RefreshFirstPersonViewEvent(), null);
    }
}
