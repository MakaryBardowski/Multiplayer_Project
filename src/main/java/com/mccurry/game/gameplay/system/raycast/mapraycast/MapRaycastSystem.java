package com.mccurry.game.gameplay.system.raycast.mapraycast;

import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.system.raycast.RaycastService;
import com.mccurry.game.gameplay.system.raycast.mapraycast.model.MapRaycastResult;
import com.mccurry.game.gameplay.system.raycast.model.RaycastResultSelector;
import com.mccurry.map.visuals.BlockWorld;
import org.jetbrains.annotations.Nullable;

public class MapRaycastSystem {

    private final BlockWorld blockWorld;
    private final RaycastService raycastService;

    public MapRaycastSystem(BlockWorld blockWorld, RaycastService raycastService){
        this.blockWorld = blockWorld;
        this.raycastService = raycastService;
    }

    public @Nullable MapRaycastResult getOneRaycastResult(Vector3f from, Vector3f direction, RaycastResultSelector selector){
        var ray = new Ray(from.clone(), direction.normalize());
        var raycastResult = raycastService.getOneRaycastResult(ray, blockWorld.getWorldNode(), selector);

        if(raycastResult == null){
            return null;
        }
        var contactPoint = raycastResult.getContactPoint();
        var contactNormal = raycastResult.getContactNormal();
        var hitBlockPosition = contactPoint.clone()
                .add(
                        contactNormal.normalizeLocal().negate().multLocal(0.1f)
                );

        var blockType = blockWorld.getBlockTypeAtPosition(hitBlockPosition);

        if(blockType == null){
            return null;
        }

        return new MapRaycastResult(contactPoint, contactNormal, blockType);
    }
}
