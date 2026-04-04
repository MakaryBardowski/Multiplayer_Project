package com.mccurry.game.gameplay.system.attack.component;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class WeaponComponent implements Component {
    private float baseDamage;
    private float cooldownSeconds;
    private double lastUsedGameTimeSeconds;
}
