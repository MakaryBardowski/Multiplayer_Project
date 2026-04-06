package com.mccurry.game.gameplay.system.equipment.component;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.system.equipment.model.BodySlot;
import com.mccurry.game.gameplay.system.equipment.model.HandSlot;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Set;

@Getter
@Builder
public class EquippableComponent implements Component {
    @Singular
    private final Set<HandSlot> handSlots;
    @Singular
    private final Set<BodySlot> bodySlots;
}
