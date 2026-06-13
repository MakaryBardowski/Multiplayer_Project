package com.mccurry.game.gameplay.system.raycast.entityraycast.component;


import com.jme3.scene.Spatial;
import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.system.raycast.entityraycast.model.EntityRaycastHitbox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class EntityRaycastHitboxComponent implements Component {
    private final @NotNull Spatial broadphaseSpatial;
    @NotNull private final List<@NotNull EntityRaycastHitbox> hitboxes = new ArrayList<>();
}
