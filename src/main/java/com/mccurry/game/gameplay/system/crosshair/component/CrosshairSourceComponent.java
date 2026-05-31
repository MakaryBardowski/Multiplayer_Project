package com.mccurry.game.gameplay.system.crosshair.component;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.system.crosshair.model.CrosshairType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class CrosshairSourceComponent implements Component {
    @NonNull
    @Builder.Default
    private CrosshairType crosshairType = CrosshairType.DEFAULT;
    private float spreadOutTimeSeconds;
    private float spreadInTimeSeconds;
}
