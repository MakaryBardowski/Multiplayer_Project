package com.mccurry.game.gameplay.system.raycast.entityraycast.model;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class EntityRaycastHitbox {
    private final @NotNull Geometry geometry;
    private final @NotNull Vector3f parentOffset;
}
