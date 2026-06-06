package com.mccurry.game.gameplay.system.raycast.mapraycast.model;

import com.jme3.math.Vector3f;
import com.mccurry.map.model.BlockType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class MapRaycastResult {
    @NotNull private final BlockType blockType;
    @NotNull private final Vector3f contactPoint;
    @NotNull private final Vector3f contactNormal;
}
