package com.mccurry.game.gameplay.system.raycast.entityraycast.model;

import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.system.raycast.model.RaycastResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
public class EntityRaycastResult extends RaycastResult {
    private final int hitEntityId;


    public EntityRaycastResult(@NotNull Vector3f contactPoint, @NotNull Vector3f contactNormal, int hitEntityId) {
        super(contactPoint, contactNormal);
        this.hitEntityId = hitEntityId;
    }
}
