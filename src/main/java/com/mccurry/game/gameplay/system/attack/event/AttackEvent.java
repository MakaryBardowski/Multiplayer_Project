package com.mccurry.game.gameplay.system.attack.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttackEvent implements Event {
    private final Entity attacker;
    private final Entity weapon;
}
