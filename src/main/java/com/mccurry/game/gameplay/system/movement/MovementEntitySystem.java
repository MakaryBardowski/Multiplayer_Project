package com.mccurry.game.gameplay.system.movement;

import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.EntitySystem;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.FirstPersonViewTransformEffectsComponent;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.model.GunSwayTransformEffect;
import com.mccurry.game.gameplay.system.movement.component.VelocityComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraTransformEffectsComponent;
import com.mccurry.game.gameplay.system.player.component.camera.model.movement.headbob.HeadbobTransformEffect;
import com.mccurry.game.gameplay.system.transform.TransformComponent;

public class MovementEntitySystem extends EntitySystem {
    public MovementEntitySystem(EntityStore entityStore) {
        super(entityStore, TransformComponent.class, VelocityComponent.class);
    }

    @Override
    protected void processEntity(Entity entityWithRequiredComponents, float tpf, double currentGameTimeSeconds) {
        var transform = entityStore.getComponent(entityWithRequiredComponents, TransformComponent.class);

        var desiredVelocity = entityStore.getComponent(entityWithRequiredComponents, VelocityComponent.class).getDesiredVelocity();
        var actualVelocity = entityStore.getComponent(entityWithRequiredComponents, VelocityComponent.class).getCalculatedActualVelocity();

        if (!desiredVelocity.equals(Vector3f.ZERO)) {
            var cameraEffectsComponent = entityStore.getComponent(entityWithRequiredComponents, CameraTransformEffectsComponent.class);
            if (cameraEffectsComponent != null) {
                var existingHeadbob = cameraEffectsComponent.getSingleInstanceEffect(HeadbobTransformEffect.class);

                if (existingHeadbob == null) {
                    var headbob = new HeadbobTransformEffect();
                    headbob.setEnabled(true);
                    cameraEffectsComponent.overwriteOrAddSingleInstanceEffect(
                            headbob
                    );
                }else{
                    existingHeadbob.setEnabled(true);
                }
            }




            var firstPersonEffectsComponent = entityStore.getComponent(entityWithRequiredComponents, FirstPersonViewTransformEffectsComponent.class);
            if(firstPersonEffectsComponent != null){
                var existingHeadbob = firstPersonEffectsComponent.getSingleInstanceEffect(GunSwayTransformEffect.class);

                if (existingHeadbob == null) {
                    var headbob = new GunSwayTransformEffect();
                    headbob.setEnabled(true);
                    firstPersonEffectsComponent.overwriteOrAddSingleInstanceEffect(
                            headbob
                    );
                }else{
                    existingHeadbob.setEnabled(true);
                }
            }


        } else {
            var cameraEffectsComponent = entityStore.getComponent(entityWithRequiredComponents, CameraTransformEffectsComponent.class);
            if (cameraEffectsComponent != null) {
                var headbob = cameraEffectsComponent.getSingleInstanceEffects().get(HeadbobTransformEffect.class);
                if (headbob != null) {
                    headbob.setEnabled(false);
                }
            }




            var firstPersonEffectsComponent = entityStore.getComponent(entityWithRequiredComponents, FirstPersonViewTransformEffectsComponent.class);
            if (firstPersonEffectsComponent != null) {
                var headbob = firstPersonEffectsComponent.getSingleInstanceEffects().get(GunSwayTransformEffect.class);
                if (headbob != null) {
                    headbob.setEnabled(false);
                }
            }
        }

        actualVelocity.set(desiredVelocity.x, desiredVelocity.y, desiredVelocity.z);

        if(!actualVelocity.equals(Vector3f.ZERO)) {
            transform.getOriginNode().move(actualVelocity.x * tpf, actualVelocity.y * tpf, actualVelocity.z * tpf);
        }
    }
}
