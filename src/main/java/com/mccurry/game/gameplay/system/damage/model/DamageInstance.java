package com.mccurry.game.gameplay.system.damage.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class DamageInstance {
    private boolean cancelled;
    @NotNull private final DamageTypeInstance physical;

    public DamageInstance(@NotNull DamageTypeInstance physical){
        this.physical = physical;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class DamageTypeInstance{
        private boolean cancelled;
        private float damageValue;

        public DamageTypeInstance(){
            this(0);
        }

        public DamageTypeInstance(float damageValue){
            this.damageValue = damageValue;
            cancelled = false;
        }
    }
}
