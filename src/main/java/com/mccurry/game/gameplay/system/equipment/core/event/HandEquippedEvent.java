package com.mccurry.game.gameplay.system.equipment.core.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HandEquippedEvent implements Event {
    private final Entity owner;
    private final Entity itemInHands;
    private final HandSlot targetSlot;
}
