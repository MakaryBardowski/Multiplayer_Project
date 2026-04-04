package com.mccurry.game.gameplay.system.equipment.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EquipWeaponEvent implements Event {
    private final Entity owner;
    private final Entity weapon;
}
