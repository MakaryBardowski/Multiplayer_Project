package com.mccurry.game.gameplay.system.movement;

import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.System;
import com.mccurry.game.gameplay.system.movement.component.VelocityComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraEffectsComponent;
import com.mccurry.game.gameplay.system.player.component.camera.model.movement.headbob.HeadbobCameraEffect;
import com.mccurry.game.gameplay.system.player.component.camera.model.movement.headbob.HeadbobToIdleCameraEffect;
import com.mccurry.game.gameplay.system.transform.TransformComponent;

public class MovementSystem extends System {
    public MovementSystem(EntityStore entityStore) {
        super(entityStore, TransformComponent.class, VelocityComponent.class);
    }

    @Override
    protected void processEntity(Entity entityWithRequiredComponents, float tpf) {
        var transform = entityStore.getComponent(entityWithRequiredComponents, TransformComponent.class);

        var desiredVelocity = entityStore.getComponent(entityWithRequiredComponents, VelocityComponent.class).getDesiredVelocity();
        var actualVelocity = entityStore.getComponent(entityWithRequiredComponents, VelocityComponent.class).getCalculatedActualVelocity();

        if (!desiredVelocity.equals(Vector3f.ZERO)) {
            var cameraEffectsComponent = entityStore.getComponent(entityWithRequiredComponents, CameraEffectsComponent.class);
            if (cameraEffectsComponent != null) {
                if (!cameraEffectsComponent.hasSingleInstanceEffect(HeadbobCameraEffect.class)) {
                    cameraEffectsComponent.addSingleInstanceEffect(
                            new HeadbobCameraEffect()
                    );
                }
            }
        } else {
            var cameraEffectsComponent = entityStore.getComponent(entityWithRequiredComponents, CameraEffectsComponent.class);
            if (cameraEffectsComponent != null) {
                var headBob = cameraEffectsComponent.removeSingleInstanceEffect(HeadbobCameraEffect.class);
                if(headBob != null) {
                    cameraEffectsComponent.addMultiInstanceEffect(new HeadbobToIdleCameraEffect(headBob.getTotalOffsetFromHeadbob()));
                }
            }
        }

        actualVelocity.set(desiredVelocity.x, desiredVelocity.y, desiredVelocity.z);
        transform.getOriginNode().move(actualVelocity.x * tpf, actualVelocity.y * tpf, actualVelocity.z * tpf);
    }
}
