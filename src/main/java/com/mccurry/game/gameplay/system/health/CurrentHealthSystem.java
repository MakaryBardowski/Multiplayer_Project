package com.mccurry.game.gameplay.system.health;


import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.core.ecs.system.System;
import com.mccurry.game.gameplay.system.health.component.CurrentHealthComponent;

public class CurrentHealthSystem extends System {

    public CurrentHealthSystem(EntityStore entityStore) {
        super(entityStore, CurrentHealthComponent.class);
    }

    @Override
    protected void processEntity(Entity entityWithRequiredComponents, float tpf) {
        var currentHealth = entityStore.getComponent(entityWithRequiredComponents, CurrentHealthComponent.class);

        var currentHealthAfterChange = 0;
        currentHealth.setCurrentHealth(currentHealthAfterChange);
    }
}
