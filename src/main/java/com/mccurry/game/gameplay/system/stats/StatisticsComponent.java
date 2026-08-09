package com.mccurry.game.gameplay.system.stats;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.system.stats.health.Health;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@Builder
public class StatisticsComponent implements Component {
    @Nullable private Health health;
}
