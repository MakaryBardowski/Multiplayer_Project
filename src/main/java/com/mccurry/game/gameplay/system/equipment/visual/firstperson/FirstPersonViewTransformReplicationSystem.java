package com.mccurry.game.gameplay.system.equipment.visual.firstperson;

import com.jme3.scene.Node;
import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.EntitySystem;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.FirstPersonViewSourceComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;

public class FirstPersonViewTransformReplicationSystem extends EntitySystem {

    private final Node firstPersonViewParentNode;

    public FirstPersonViewTransformReplicationSystem(Node firstPersonViewParentNode, EntityStore entityStore, Class<? extends Component>... requiredComponents) {
        super(entityStore, requiredComponents);
        this.firstPersonViewParentNode = firstPersonViewParentNode;
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



        firstPersonViewParentNode.setLocalTranslation(
                transform.getOriginNode().getLocalTranslation().add(cameraHolderComponent.getCurrentCameraOffset())
        );

        firstPersonViewParentNode.setLocalRotation(cameraHolderComponent.getCurrentCameraRotation());
    }
}
