package com.mccurry.game.gameplay.system.player;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.SingletonEntitySystem;
import com.mccurry.game.gameplay.system.player.component.camera.CameraTransformEffectsComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;

public class CameraSystem extends SingletonEntitySystem {
    private final Camera camera;

    public CameraSystem(EntityStore entityStore, Camera camera) {
        super(entityStore, CameraHolderComponent.class);
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entityWithRequiredComponents, float tpf,double currentGameTimeSeconds) {
        var cameraComponent = entityStore.getComponent(entityWithRequiredComponents, CameraHolderComponent.class);

        var transformComponent = entityStore.getComponent(entityWithRequiredComponents, TransformComponent.class);
        if (transformComponent == null) {
            return;
        }

        var originalOffset = cameraComponent.getCameraRestOffset();
        var baseOffsetAfterEffectsLocal = cameraComponent.getCameraRestOffset().clone();
        var currentRotationAfterEffects = cameraComponent.getCurrentDesiredCameraRotation().clone();

        var cameraEffectsComponent = entityStore.getComponent(entityWithRequiredComponents, CameraTransformEffectsComponent.class);
        if (cameraEffectsComponent != null) {
            updateSingleInstanceEffects(cameraEffectsComponent, baseOffsetAfterEffectsLocal, currentRotationAfterEffects);
            updateMultiInstanceEffects(cameraEffectsComponent, baseOffsetAfterEffectsLocal, currentRotationAfterEffects);
        }

        var worldOffset = currentRotationAfterEffects.mult(baseOffsetAfterEffectsLocal.subtract(originalOffset));
        camera.setLocation(transformComponent.getOriginNode().getWorldTranslation().add(worldOffset.addLocal(originalOffset)));
        camera.setRotation(currentRotationAfterEffects);
        cameraComponent.getCurrentActualCameraRotation().set(currentRotationAfterEffects);
    }

    private void updateMultiInstanceEffects(CameraTransformEffectsComponent cameraTransformEffectsComponent, Vector3f cameraOffset, Quaternion cameraRotation) {
        var multiInstanceEffects = cameraTransformEffectsComponent.getMultiInstanceEffects();
        var multiInstanceIterator = multiInstanceEffects.iterator();
        while (multiInstanceIterator.hasNext()) {
            var effect = multiInstanceIterator.next();

            if (effect.isCleanup()) {
                multiInstanceIterator.remove();
            }

            effect.applyPosition(cameraOffset);
            effect.applyRotation(cameraRotation);
        }
    }

    private void updateSingleInstanceEffects(CameraTransformEffectsComponent cameraTransformEffectsComponent, Vector3f cameraOffset, Quaternion cameraRotation) {
        var singleInstanceEffects = cameraTransformEffectsComponent.getSingleInstanceEffects();
        var singleInstanceIterator = singleInstanceEffects.entrySet().iterator();
        while (singleInstanceIterator.hasNext()) {
            var effect = singleInstanceIterator.next().getValue();
            if (effect.isCleanup()) {
                singleInstanceIterator.remove();
            }
            effect.applyPosition(cameraOffset);
            effect.applyRotation(cameraRotation);
        }
    }
}
