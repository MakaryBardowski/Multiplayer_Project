package com.mccurry.game.gameplay.system.stats.health;

import org.jetbrains.annotations.NotNull;

public class HealthCalculationSystem {

    public void damage(@NotNull Health targetHealth, float value){
        if(value == 0){
            return;
        }

        if(targetHealth.getCurrent() == 0){
            return;
        }

        targetHealth.setCurrent(
                Math.max(targetHealth.getCurrent()-value,0)
        );
    }
}
