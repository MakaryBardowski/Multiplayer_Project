package com.mccurry.game.gameplay.system.stats.health;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
@Builder
public class Health {
    private float base;
    private float current;
    private float max;
}
