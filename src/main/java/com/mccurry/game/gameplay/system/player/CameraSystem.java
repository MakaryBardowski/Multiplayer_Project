package com.mccurry.game.gameplay.system.player;

import com.jme3.renderer.Camera;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.SingletonSystem;
import com.mccurry.game.gameplay.system.player.component.CameraHolderComponent;
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
        if(transformComponent == null){
            return;
        }

        var currentOffset = cameraComponent.getCurrentCameraOffset();

        camera.setLocation(transformComponent.getOriginNode().getWorldTranslation().add(currentOffset));
        camera.setRotation(cameraComponent.getCurrentCameraRotation());
    }
}
