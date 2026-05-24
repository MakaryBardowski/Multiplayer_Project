package com.mccurry.game.gameplay.system.attack.component.viewmodel;

import com.jme3.math.Vector3f;
import com.mccurry.game.shared.sfx.model.VfxType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
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
