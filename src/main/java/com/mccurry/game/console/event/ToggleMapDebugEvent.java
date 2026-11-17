package com.mccurry.game.console.event;

import com.mccurry.events.core.Event;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ToggleMapDebugEvent implements Event {
    private final MapDebugType mapDebugType;

    public enum MapDebugType{
        CHUNK
    };
}
