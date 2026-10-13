package com.mccurry.game.gameplay.system.attack.spread.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class SpreadData {
    @NotNull private final SpreadData.Spread minimumSpread;
    @NotNull private final SpreadData.Spread maximumSpread;
    @NotNull private final Heat heat;

    @Data
    public static class Spread {
        private final float angle;
    }

    @Data
    public static class Heat {
        private float level;
        private final float maxLevel;

        private final float heatChangePerActivation;

        private final float levelChangeRatePerSecond; // immutable because it would require refresh of crosshair
    }
}
