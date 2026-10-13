package com.mccurry.game.gameplay.system.attack.core.component;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.system.attack.spread.model.SpreadData;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
@Builder
public class WeaponIdentityComponent implements Component {
    @Nullable private WeaponActionIdentity primaryActionIdentity;
    @Nullable private  WeaponActionIdentity secondaryActionIdentity;
    @Nullable private SpreadData spreadData;

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class WeaponActionIdentity{
        private float cooldownSeconds;
        private double lastUsedGameTimeSeconds;

        @NotNull private WeaponProjectileSpawnConfig projectileConfig;

        public WeaponActionIdentity(
                float cooldownSeconds,
                WeaponProjectileSpawnConfig projectileConfig
        ){
            this(
                    cooldownSeconds,
                    Double.MIN_VALUE,
                    projectileConfig
            );
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class WeaponProjectileSpawnConfig {
            @NotNull private ProjectileIdentity projectileIdentity;
            @NotNull private ProjectileRelativeSpreadStrategy relativeSpreadStrategy;
            @NotNull private ProjectileType projectileType;
            private int projectileCount;

            @Getter
            @Setter
            @AllArgsConstructor
            public static class ProjectileIdentity{
                private float damage;
            }

            public enum ProjectileRelativeSpreadStrategy{
                FULL_RANDOM,
                SUNFLOWER
            }

            public enum ProjectileType {
                HITSCAN
            }
        }
    }
}
