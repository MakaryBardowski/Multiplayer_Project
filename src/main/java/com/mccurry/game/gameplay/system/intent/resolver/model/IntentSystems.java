package com.mccurry.game.gameplay.system.intent.resolver.model;

import com.mccurry.game.gameplay.system.attack.AttackSystem;
import com.mccurry.game.gameplay.system.equipment.EquipSystem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class IntentSystems {
    @NotNull private AttackSystem attackSystem;
    @NotNull private EquipSystem equipSystem;
}
