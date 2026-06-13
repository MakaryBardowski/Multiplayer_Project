package com.mccurry.game.gameplay.system.raycast.entityraycast.model;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityCallback;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.raycast.RaycastService;
import com.mccurry.game.gameplay.system.raycast.entityraycast.component.EntityRaycastHitboxComponent;
import com.mccurry.game.gameplay.system.raycast.model.RaycastResultSelector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RaycastEntityCallback implements EntityCallback {
    private final RaycastService raycastService;
    private final EntityStore entityStore;

    private final CollisionResults mutableResults = new CollisionResults();
    private final Set<Integer> excludedEntities = new HashSet<>();
    private Ray currentRay;

    public RaycastEntityCallback(EntityStore entityStore, RaycastService raycastService){
        this.entityStore = entityStore;
        this.raycastService = raycastService;
    }

    @Override
    public void execute(Entity entityWithRequiredComponents, float tpf, double currentGameTimeInSeconds) {
        if(excludedEntities.contains(entityWithRequiredComponents.getId())){
            return;
        }
        var component = entityStore.getComponent(entityWithRequiredComponents, EntityRaycastHitboxComponent.class);
        var bv = component.getBroadphaseSpatial().getWorldBound();
        var intersects = bv.intersects(currentRay);
        if(intersects){
            for(var hitbox : component.getHitboxes()) {
                var hitboxGeom = hitbox.getGeometry();
                raycastService.getOneRaycastResult(currentRay, hitboxGeom, RaycastResultSelector.ANY, mutableResults);
            }
        }
    }

    public void excludingEntity(int id){
        excludedEntities.add(id);
    }

    public void checkingAgainstRay(@NotNull Ray ray){
        this.currentRay = ray;
    }

    public void prepareForNextRaycast(){
        mutableResults.clear();
        excludedEntities.clear();
        currentRay = null;
    }

    public @Nullable EntityRaycastResult acquireResult(RaycastResultSelector selector){
        if(mutableResults.size() == 0){
            return null;
        }
        var raycastResult =  switch (selector){
            case ANY -> mutableResults.getCollision(0);
            case CLOSEST -> mutableResults.getClosestCollision();
            case FARTHEST -> mutableResults.getFarthestCollision();
        };

        return new EntityRaycastResult(
                raycastResult.getContactPoint(),
                raycastResult.getContactNormal(),
                raycastResult.getGeometry().getUserData("hitboxOwnerEntityId")
        );
    }
}
