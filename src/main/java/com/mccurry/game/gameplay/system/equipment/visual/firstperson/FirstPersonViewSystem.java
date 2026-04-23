package com.mccurry.game.gameplay.system.equipment.visual.firstperson;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.EntitySystem;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.FirstPersonViewSourceComponent;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.FirstPersonViewTransformEffectsComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;

/**
 * Manages the first person model effects (sway etc) and replication of position/rotation of parent entity
 */
public class FirstPersonViewSystem extends EntitySystem {

    /**
    node to which the parent node of hands is attached
     */
    private final Node firstPersonViewParentNode;

    /**
    parent node of the hands
     */
    private final Node firstPersonNode;
    private Vector3f baseNodePosition;
    private Quaternion baseNodeRotation;

    public FirstPersonViewSystem(
            Node firstPersonViewParentNode,
            Node firstPersonNode,
            EntityStore entityStore,
            Class<? extends Component>... requiredComponents
    ) {
        super(entityStore, requiredComponents);
        this.firstPersonViewParentNode = firstPersonViewParentNode;
        this.firstPersonNode = firstPersonNode;
        this.baseNodePosition = firstPersonNode.getLocalTranslation().clone();
        this.baseNodeRotation = firstPersonNode.getLocalRotation().clone();

    }

    @Override
    public void processEligibleEntities(float tpf, double currentGameTime) {
        var fpViewSourceEntity = entityStore.getSingletonComponentHolder(FirstPersonViewSourceComponent.class);
        if(fpViewSourceEntity == null){
            return;
        }
        processEntity(fpViewSourceEntity, tpf, currentGameTime);
    }

    @Override
    protected void processEntity(Entity entityWithRequiredComponents, float tpf, double currentGameTime) {
        var transform = entityStore.getComponent(entityWithRequiredComponents, TransformComponent.class);

        if(transform == null){
            return;
        }

        var cameraHolderComponent = entityStore.getComponent(entityWithRequiredComponents, CameraHolderComponent.class);
        if(cameraHolderComponent == null){
            return;
        }

        // the parent always follows player
        firstPersonViewParentNode.setLocalTranslation(
                transform.getOriginNode().getLocalTranslation().add(cameraHolderComponent.getCurrentCameraOffset())
        );
        firstPersonViewParentNode.setLocalRotation(cameraHolderComponent.getCurrentCameraRotation());
        // the parent always follows player


        var firstPersonViewEffects = entityStore.getComponent(entityWithRequiredComponents, FirstPersonViewTransformEffectsComponent.class);

        if(firstPersonViewEffects == null){
            return;
        }

        var baseOffsetAfterEffects = baseNodePosition.clone();
        var baseRotationAfterEffects = baseNodeRotation.clone();
        updateSingleInstanceEffects(firstPersonViewEffects, baseOffsetAfterEffects, baseRotationAfterEffects);
        updateMultiInstanceEffects(firstPersonViewEffects, baseOffsetAfterEffects, baseRotationAfterEffects);

        firstPersonNode.setLocalTranslation(baseOffsetAfterEffects);
        firstPersonNode.setLocalRotation(baseRotationAfterEffects);
    }

    private void updateMultiInstanceEffects(FirstPersonViewTransformEffectsComponent firstPersonViewTransformEffectsComponent, Vector3f firstPersonViewOffset, Quaternion firstPersonViewRotation) {
        var multiInstanceEffects = firstPersonViewTransformEffectsComponent.getMultiInstanceEffects();
        var multiInstanceIterator = multiInstanceEffects.iterator();
        while (multiInstanceIterator.hasNext()) {
            var effect = multiInstanceIterator.next();

            if (effect.isCleanup()) {
                multiInstanceIterator.remove();
            }

            effect.applyPosition(firstPersonViewOffset);
            effect.applyRotation(firstPersonViewRotation);
        }
    }

    private void updateSingleInstanceEffects(FirstPersonViewTransformEffectsComponent firstPersonViewTransformEffectsComponent, Vector3f firstPersonViewOffset, Quaternion firstPersonViewRotation) {
        var singleInstanceEffects = firstPersonViewTransformEffectsComponent.getSingleInstanceEffects();
        var singleInstanceIterator = singleInstanceEffects.entrySet().iterator();
        while (singleInstanceIterator.hasNext()) {
            var effect = singleInstanceIterator.next().getValue();
            if (effect.isCleanup()) {
                singleInstanceIterator.remove();
            }
            effect.applyPosition(firstPersonViewOffset);
            effect.applyRotation(firstPersonViewRotation);
        }
    }
}
