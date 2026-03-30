package com.mccurry.game.gameplay.core.ecs.system;

import com.mccurry.game.gameplay.core.ecs.component.SingletonComponent;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;

public abstract class SingletonSystem extends System{
    private final Class<? extends SingletonComponent> requiredComponent;
    public SingletonSystem(EntityStore entityStore, Class<? extends SingletonComponent> requiredComponent) {
        super(entityStore, requiredComponent);
        this.requiredComponent = requiredComponent;
    }

    @Override
    public void processEligibleEntities(float tpf) {
        var entity = entityStore.getSingletonComponentHolder(requiredComponent);
        if(entity == null){
            return;
        }
        processEntity(entity,tpf);
    }
}
