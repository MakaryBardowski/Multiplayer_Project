package com.mccurry.game.gameplay.system.raycast.mapraycast.model;

import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.system.raycast.model.RaycastResult;
import com.mccurry.map.model.BlockType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class MapRaycastResult extends RaycastResult {
    @NotNull private final BlockType blockType;

    public MapRaycastResult(@NotNull Vector3f contactPoint, @NotNull Vector3f contactNormal, @NotNull BlockType blockType) {
        super(contactPoint, contactNormal);
        this.blockType = blockType;
    }
}
