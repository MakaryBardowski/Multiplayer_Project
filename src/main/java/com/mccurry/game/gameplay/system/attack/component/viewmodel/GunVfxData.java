package com.mccurry.game.gameplay.system.attack.component.viewmodel;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.mccurry.game.shared.sfx.model.VfxType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Builder
public class GunVfxData {
    @Nullable private VfxType muzzleFlash;
    @Nullable private VfxType caseEjection;

    @Builder.Default
    @NotNull private Vector3f muzzleFlashOffset = new Vector3f();

    @Nullable private Node muzzleNode;
    @Nullable private Node bulletCaseEjectionNode;
}
