package com.mccurry.game.gameplay.system.crosshair.event;

import com.mccurry.events.core.Event;
import lombok.Data;

@Data
public class CrosshairSpreadRequestEvent implements Event {
    float newAngle;
}
