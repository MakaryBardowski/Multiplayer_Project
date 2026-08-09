package com.mccurry.game.gameplay.system.damage.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
public class DamageResult {
    @NotNull private final Integer targetEntityId;
    private boolean damageCancelled;
    private boolean targetDied;
}
