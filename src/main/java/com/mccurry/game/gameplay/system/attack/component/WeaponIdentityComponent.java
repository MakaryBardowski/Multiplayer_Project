package com.mccurry.game.gameplay.system.attack.component;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
@Builder
public class WeaponIdentityComponent implements Component {
    @Nullable private WeaponActionIdentity primaryActionIdentity;
    @Nullable private  WeaponActionIdentity secondaryActionIdentity;



    @AllArgsConstructor
    @Getter
    @Setter
    public static class WeaponActionIdentity{
        private float baseDamage;
        private float cooldownSeconds;
        private double lastUsedGameTimeSeconds;
    }
}
