package com.mccurry.game.gameplay.system.stats.health.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Health {
    private float base;
    private float current;
    private float max;
}
