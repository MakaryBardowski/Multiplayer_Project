package com.mccurry.game.gameplay.system.raycast.model;

import com.jme3.math.Vector3f;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public abstract class RaycastResult {
    @NotNull private final Vector3f contactPoint;
    @NotNull private final Vector3f contactNormal;
}
