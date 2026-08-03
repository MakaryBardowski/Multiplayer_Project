package com.mccurry.game.gameplay.system.raycast.entityraycast.model;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.mccurry.game.gameplay.system.hitfeedback.model.HitFeedback;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public class EntityRaycastHitbox {
    private final @NotNull Geometry geometry;
    private final @NotNull Vector3f parentOffset;
    private final @Nullable HitFeedback hitFeedback;
}
