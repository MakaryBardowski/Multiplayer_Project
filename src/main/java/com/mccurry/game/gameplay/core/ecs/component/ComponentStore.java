package com.mccurry.game.gameplay.core.ecs.component;

import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ComponentStore {
    private final Map<Integer, List<Component>> entityComponentsByEntityId = new HashMap<>();

    public <T extends Component> @Nullable T getComponent(Entity entity, Class<T> componentClass) {
        var entityComponents = entityComponentsByEntityId.get(entity.getId());
        if (entityComponents == null) {
            return null;
        }

        Component tempComponent;
        for (int i = 0; i < entityComponents.size(); i++) {
            tempComponent = entityComponents.get(i);
            if (tempComponent.getClass().equals(componentClass)) {
                return (T) tempComponent;
            }
        }

        return null;
    }

    public void addComponent(Entity entity, Component component){
        var entityId = entity.getId();
        var existingComponents = entityComponentsByEntityId.get(entityId);
        if (existingComponents == null) {
            entityComponentsByEntityId.put(entityId, new ArrayList<>() {{
                            add(component);
                    }}
            );
            return;
        }
        existingComponents.add(component);
    }

    public void removeComponent(Entity entity, Class<? extends Component> componentClass){
        var entityId = entity.getId();
        var existingComponents = entityComponentsByEntityId.get(entityId);
        if (existingComponents == null) {
            return;
        }

        existingComponents.removeIf(component -> component.getClass().equals(componentClass));
    }
}
