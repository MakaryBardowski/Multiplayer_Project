package com.mccurry.game.gameplay.system.attack.component.viewmodel;


import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.mccurry.game.gameplay.core.ecs.component.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class GunViewModelComponent implements Component {
    @Nullable private Spatial viewModel;

    @Nullable private Node muzzleNode;
    @Nullable private Node bulletCaseEjectionNode;

    @Nullable private GunVfxData gunVfxData;
}
