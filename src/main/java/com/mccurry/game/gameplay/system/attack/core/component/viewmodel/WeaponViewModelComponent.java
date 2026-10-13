package com.mccurry.game.gameplay.system.attack.core.component.viewmodel;


import com.jme3.anim.AnimComposer;
import com.jme3.scene.Spatial;
import com.mccurry.game.gameplay.core.ecs.component.Component;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Builder
@Getter
public class WeaponViewModelComponent implements Component {
    @Nullable private Spatial viewModel;
    @Nullable private AnimComposer animComposer;
    @Nullable private GunAnimationData gunAnimationData;

    @Nullable private GunVfxData primaryActionVfxData;
    @Nullable private GunVfxData secondaryVfxData;
}
