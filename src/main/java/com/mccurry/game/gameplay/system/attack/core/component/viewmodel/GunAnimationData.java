package com.mccurry.game.gameplay.system.attack.core.component.viewmodel;

import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@Builder
public class GunAnimationData {
    @Nullable
    private String lockRecoilAnimName;

    @Nullable
    private String lockReturnAnimName;

    @Nullable
    private String lockIdleAnimName;
}
