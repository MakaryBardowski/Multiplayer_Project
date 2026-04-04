package com.mccurry.game.gameplay.system.player;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.SingletonSystem;
import com.mccurry.game.gameplay.system.player.component.camera.CameraEffectsComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;

public class CameraSystem extends SingletonSystem {
    private final Camera camera;

    public CameraSystem(EntityStore entityStore, Camera camera) {
        super(entityStore, CameraHolderComponent.class);
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entityWithRequiredComponents, float tpf) {
        var cameraComponent = entityStore.getComponent(entityWithRequiredComponents, CameraHolderComponent.class);

        var transformComponent = entityStore.getComponent(entityWithRequiredComponents, TransformComponent.class);
        if (transformComponent == null) {
            return;
        }

        var currentOffset = cameraComponent.getCurrentCameraOffset();
        var currentRotation = cameraComponent.getCurrentCameraRotation();

        var cameraEffectsComponent = entityStore.getComponent(entityWithRequiredComponents, CameraEffectsComponent.class);
        if (cameraEffectsComponent != null) {
            updateSingleInstanceEffects(cameraEffectsComponent, currentOffset, currentRotation);
            updateMultiInstanceEffects(cameraEffectsComponent, currentOffset, currentRotation);
        }

        camera.setLocation(transformComponent.getOriginNode().getWorldTranslation().add(currentOffset));


        camera.setRotation(cameraComponent.getCurrentCameraRotation());
    }

    private void updateMultiInstanceEffects(CameraEffectsComponent cameraEffectsComponent, Vector3f cameraOffset, Quaternion cameraRotation) {
        var multiInstanceEffects = cameraEffectsComponent.getMultiInstanceEffects();
        var multiInstanceIterator = multiInstanceEffects.iterator();
        while (multiInstanceIterator.hasNext()) {
            var effect = multiInstanceIterator.next();

            if (effect.isFinished()) {
                multiInstanceIterator.remove();
//                var chained = effect.getChainedEffect(cameraOffset);
//                if(chained != null){
//                    cameraEffectsComponent.chainMultiInstance(chained);
//                }
            }

            effect.applyPosition(cameraOffset);
            effect.applyRotation(cameraRotation);
        }

        var chainedMultiIterator = cameraEffectsComponent.getMultiInstanceChainedEffectsToAdd().iterator();
        while (chainedMultiIterator.hasNext()) {
            var chainedEffect = chainedMultiIterator.next();
            cameraEffectsComponent.addMultiInstanceEffect(chainedEffect);
            chainedMultiIterator.remove();
        }
    }

    private void updateSingleInstanceEffects(CameraEffectsComponent cameraEffectsComponent, Vector3f cameraOffset, Quaternion cameraRotation) {
        var singleInstanceEffects = cameraEffectsComponent.getSingleInstanceEffects();
        var singleInstanceIterator = singleInstanceEffects.entrySet().iterator();
        while (singleInstanceIterator.hasNext()) {
            var effect = singleInstanceIterator.next().getValue();
            if (effect.isFinished()) {
                singleInstanceIterator.remove();
//                var chained = effect.getChainedEffect(cameraOffset);
//                if(chained != null){
//                    cameraEffectsComponent.chainSingleInstance(chained);
//                }
            }
            effect.applyPosition(cameraOffset);
            effect.applyRotation(cameraRotation);
        }

        var chainedSingleiterator = cameraEffectsComponent.getSingleInstanceChainedEffectsToAdd().iterator();
        while (chainedSingleiterator.hasNext()) {
            var chainedEffect = chainedSingleiterator.next();
            cameraEffectsComponent.addSingleInstanceEffect(chainedEffect);
            chainedSingleiterator.remove();
        }
    }
}
