package com.mccurry.game.gameplay.system.crosshair.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum CrosshairType {
    DEFAULT(null, null, "Interface/GUI/Crosshair/smg/smg_center.png"),
    SMG("Interface/GUI/Crosshair/smg/smg_right.png", "Interface/GUI/Crosshair/smg/smg_left.png", "Interface/GUI/Crosshair/smg/smg_center.png"),
    SHOTGUN("Interface/GUI/Crosshair/shotgun/shotgun_left.png", "Interface/GUI/Crosshair/shotgun/shotgun_right.png", "Interface/GUI/Crosshair/shotgun/shotgun_center.png");

    @Nullable private final String rightTexturePath;
    @Nullable private final String leftTexturePath;
    @Nullable private final String centerTexturePath;
}
