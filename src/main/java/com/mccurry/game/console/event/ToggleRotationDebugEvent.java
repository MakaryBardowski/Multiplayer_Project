package com.mccurry.game.console.event;

import com.mccurry.events.core.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ToggleRotationDebugEvent implements Event {
    private final boolean granular;
}
