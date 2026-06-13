package com.mccurry.game.gameplay.system.raycast;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.mccurry.game.gameplay.system.raycast.model.RaycastResultSelector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RaycastService {

    public @Nullable CollisionResult getOneRaycastResult(
            @NotNull Ray ray,
            @NotNull Spatial spatial,
            @NotNull RaycastResultSelector selector
    ){
        return getOneRaycastResult(ray,spatial,selector,null);
    }

    public @Nullable CollisionResult getOneRaycastResult(
            @NotNull Ray ray,
            @NotNull Spatial spatial,
            @NotNull RaycastResultSelector selector,
            @Nullable CollisionResults store
    ){
        var results = store;
        if(results == null){
            results = new CollisionResults();
        }

        spatial.collideWith(ray, results);

        if(results.size() == 0){
            return null;
        }

        return switch (selector){
            case ANY -> results.getCollision(0);
            case CLOSEST -> results.getClosestCollision();
            case FARTHEST -> results.getFarthestCollision();
        };
    }
}
