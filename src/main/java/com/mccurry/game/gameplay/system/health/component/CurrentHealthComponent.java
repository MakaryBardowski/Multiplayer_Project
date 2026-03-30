package com.mccurry.game.gameplay.system.health.component;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class CurrentHealthComponent implements Component {
    @Setter
    private float currentHealth;
}
