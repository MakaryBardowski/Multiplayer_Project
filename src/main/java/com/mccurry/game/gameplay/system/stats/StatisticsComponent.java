package com.mccurry.game.gameplay.system.stats;

import com.mccurry.game.gameplay.system.stats.health.model.Health;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

@Builder
public class StatisticsComponent {
    @Nullable private Health health;
}
