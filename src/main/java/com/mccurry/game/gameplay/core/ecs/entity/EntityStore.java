package com.mccurry.game.gameplay.core.ecs.entity;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.core.ecs.component.ComponentStore;
import com.mccurry.game.gameplay.core.ecs.component.SingletonComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityStore {
    private Integer nextEntityId = 0;
    private final List<Entity> entities = new ArrayList<>();
    private final ComponentStore componentStore = new ComponentStore();

    private final Map<Class<? extends SingletonComponent>, Entity> singletonComponentHolders = new HashMap<>();

    /**
     * MUST be called only from the main thread
     * @param callback
     * @param componentClasses
     */
    public void forEachEntityWithComponents(float tpf,EntityCallback callback, Class<? extends Component>... componentClasses){

        for(final var entity : entities){
            var hasAllQueriedComponents = true;

            for(final var queriedComponentClass : componentClasses){
                if(componentStore.getComponent(entity, queriedComponentClass) == null){
                    hasAllQueriedComponents = false;
                    break;
                }
            }

            if(hasAllQueriedComponents){
                callback.execute(entity,tpf);
            }

        }
    }


    public Entity create(Component... components){
        var entity = new Entity(nextEntityId++);
        entities.add(entity);
        addComponentsToEntity(entity,components);
        return entity;
    }

    public <T extends Component> @Nullable T getComponent(Entity entity, Class<T> componentClass){
        return componentStore.getComponent(entity,componentClass);
    }


    public void addComponentsToEntity(Entity entity, Component... components){
        for(var component : components) {
            if(component instanceof SingletonComponent singletonComponent){
                var singletonComponentClass = singletonComponent.getClass();
                var currentSingletonComponentHolder = singletonComponentHolders.remove(singletonComponentClass);
                if(currentSingletonComponentHolder != null){
                    componentStore.removeComponent(currentSingletonComponentHolder, singletonComponentClass);
                }
                singletonComponentHolders.put(singletonComponentClass, entity);
            }
            componentStore.addComponent(entity, component);
        }
    }

    public @Nullable Entity getSingletonComponentHolder(Class<? extends SingletonComponent> componentClass){
        return singletonComponentHolders.get(componentClass);
    }
}
