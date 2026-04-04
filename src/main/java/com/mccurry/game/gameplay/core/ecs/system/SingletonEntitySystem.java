package com.mccurry.game.gameplay.core.ecs.system;

import com.mccurry.game.gameplay.core.ecs.component.SingletonComponent;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;

public abstract class SingletonEntitySystem extends EntitySystem {
    private final Class<? extends SingletonComponent> requiredComponent;
    public SingletonEntitySystem(EntityStore entityStore, Class<? extends SingletonComponent> requiredComponent) {
        super(entityStore, requiredComponent);
        this.requiredComponent = requiredComponent;
    }

    @Override
    public void processEligibleEntities(float tpf, double currentGameTimeSeconds) {
        var entity = entityStore.getSingletonComponentHolder(requiredComponent);
        if(entity == null){
            return;
        }
        processEntity(entity,tpf,currentGameTimeSeconds);
    }
}
