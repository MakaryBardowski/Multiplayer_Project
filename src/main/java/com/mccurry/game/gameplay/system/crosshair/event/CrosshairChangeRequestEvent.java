package com.mccurry.game.gameplay.system.crosshair.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.gameplay.system.crosshair.model.CrosshairType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CrosshairChangeRequestEvent implements Event {
    private final CrosshairType newCrosshairType;
}
