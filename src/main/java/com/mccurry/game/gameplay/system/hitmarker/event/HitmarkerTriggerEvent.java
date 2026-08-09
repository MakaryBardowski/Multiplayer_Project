package com.mccurry.game.gameplay.system.hitmarker.event;

import com.mccurry.events.core.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HitmarkerTriggerEvent implements Event {
    private final boolean isKill;
}
