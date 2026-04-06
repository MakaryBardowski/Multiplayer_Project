package com.mccurry.game.console.model;

import com.mccurry.events.core.EventBus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GameContext {
    private final EventBus gameEventBus;
}
