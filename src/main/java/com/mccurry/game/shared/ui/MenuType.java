package com.mccurry.game.shared.ui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.mccurry.game.shared.ui.MenuType.MenuLayer.OVERLAY;
import static com.mccurry.game.shared.ui.MenuType.MenuLayer.ROOT;

@Getter
@RequiredArgsConstructor
public enum MenuType {
    MAIN_MENU(ROOT),
    SETTINGS(OVERLAY),
    GAMEMODE_SELECTOR(ROOT);

    private final MenuLayer menuLayer;
    public enum MenuLayer{
        ROOT,
        OVERLAY
    }
}
