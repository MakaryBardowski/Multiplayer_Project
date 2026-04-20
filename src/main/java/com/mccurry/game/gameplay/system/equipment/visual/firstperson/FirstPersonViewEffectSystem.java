package com.mccurry.game.gameplay.system.equipment.visual.firstperson;

import com.jme3.scene.Node;
import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.EntitySystem;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.FirstPersonViewSourceComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;

public class FirstPersonViewEffectSystem extends EntitySystem {

    private final Node firstPersonViewParentNode;
    private float x;

    public FirstPersonViewEffectSystem(Node firstPersonViewParentNode, EntityStore entityStore, Class<? extends Component>... requiredComponents) {
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

        //todo: move to a separate system once i optimize the ecs
        firstPersonViewParentNode.setLocalTranslation(
                transform.getOriginNode().getLocalTranslation().add(cameraHolderComponent.getCurrentCameraOffset())
        );
        firstPersonViewParentNode.setLocalRotation(cameraHolderComponent.getCurrentCameraRotation());


        x+=(1f/60)*2;
        firstPersonViewParentNode.getChild(0).setLocalTranslation(
                (float)Math.sin(x)*0.02f,
                firstPersonViewParentNode.getChild(0).getLocalTranslation().getY()+(float)Math.sin(x)*0.001f,
                firstPersonViewParentNode.getChild(0).getLocalTranslation().getZ()
        );
    }
}
