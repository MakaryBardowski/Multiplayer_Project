package com.mccurry.game.gameplay.system.equipment.core.component;

import com.jme3.scene.Node;
import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.system.equipment.core.model.BodySlot;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Getter
@Builder
public class EquippableComponent implements Component {
    @Singular
    private final Set<HandSlot> handSlots;
    @Singular
    private final Set<BodySlot> bodySlots;

    @Nullable
    private final Node displayNode;
}
