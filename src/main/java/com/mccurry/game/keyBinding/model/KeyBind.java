package com.mccurry.game.keyBinding.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode
@RequiredArgsConstructor
public class KeyBind {
    private final String keyName;
}
