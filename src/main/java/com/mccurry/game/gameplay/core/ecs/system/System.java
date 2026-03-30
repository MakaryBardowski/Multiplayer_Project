package com.mccurry.game.gameplay.core.ecs.system;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;

public abstract class System {
    protected final EntityStore entityStore;
    private final Class<? extends Component>[] components;

    public System(EntityStore entityStore, Class<? extends Component>... requiredComponents){
        this.entityStore = entityStore;
        this.components = new Class[requiredComponents.length];
        for(int i = 0; i < components.length; i++){
            this.components[i] = requiredComponents[i];
        }
    }

    public void processEligibleEntities(float tpf){
        entityStore.forEachEntityWithComponents(tpf,this::processEntity, components);
    }

    protected abstract void processEntity(Entity entityWithRequiredComponents, float tpf);
}
