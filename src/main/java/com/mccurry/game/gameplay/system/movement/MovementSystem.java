package com.mccurry.game.gameplay.system.movement;

import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.System;
import com.mccurry.game.gameplay.system.movement.component.VelocityComponent;
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

        actualVelocity.set(desiredVelocity.x,desiredVelocity.y,desiredVelocity.z);
        transform.getOriginNode().move(actualVelocity.x*tpf,actualVelocity.y*tpf,actualVelocity.z*tpf);
    }
}
