package com.mccurry.game.gameplay.system.attack.component.viewmodel;

import com.jme3.math.Vector3f;
import com.mccurry.game.shared.sfx.model.VfxType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
public class GunVfxData {
    @NonNull
    private VfxType muzzleFlash;
    @NonNull private VfxType caseEjection;


    @Builder.Default
    @NotNull private Vector3f muzzleFlashOffset = new Vector3f();


}
