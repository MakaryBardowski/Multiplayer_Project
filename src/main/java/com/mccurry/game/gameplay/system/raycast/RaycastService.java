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
            @NotNull Vector3f from,
            @NotNull Vector3f direction,
            @NotNull Spatial spatial,
            @NotNull RaycastResultSelector selector
    ){

        var ray = new Ray(from.clone(), direction.normalize());

        var results = new CollisionResults();

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
