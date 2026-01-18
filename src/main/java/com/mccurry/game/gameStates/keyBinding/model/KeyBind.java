package com.mccurry.game.gameStates.keyBinding.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KeyBind {
    private final String internalKeyBind;
    private final String uiKeyName;
}
