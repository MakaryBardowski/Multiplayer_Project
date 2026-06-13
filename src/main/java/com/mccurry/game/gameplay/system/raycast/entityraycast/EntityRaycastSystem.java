package com.mccurry.game.gameplay.system.raycast.entityraycast;

import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.raycast.RaycastService;
import com.mccurry.game.gameplay.system.raycast.entityraycast.component.EntityRaycastHitboxComponent;
import com.mccurry.game.gameplay.system.raycast.entityraycast.model.EntityRaycastResult;
import com.mccurry.game.gameplay.system.raycast.entityraycast.model.RaycastEntityCallback;
import com.mccurry.game.gameplay.system.raycast.model.RaycastResultSelector;
import org.jetbrains.annotations.Nullable;


public class EntityRaycastSystem {
    private final EntityStore entityStore;
    private final RaycastEntityCallback callback;

    public EntityRaycastSystem(EntityStore entityStore, RaycastService raycastService){
        this.entityStore = entityStore;
        this.callback = new RaycastEntityCallback(entityStore,raycastService);
    }

    public @Nullable EntityRaycastResult getOneRaycastResult(@Nullable Integer excludedEntity, Vector3f from, Vector3f direction, RaycastResultSelector selector, float tpf, double gameTimeSeconds){
        callback.prepareForNextRaycast();
        if(excludedEntity != null) {
            callback.excludingEntity(excludedEntity);
        }
        var ray = new Ray(from.clone(), direction.normalize());
        callback.checkingAgainstRay(ray);
        entityStore.forEachEntityWithComponents(tpf, gameTimeSeconds,  callback, EntityRaycastHitboxComponent.class);
        return callback.acquireResult(selector);
    }
}
