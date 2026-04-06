package com.mccurry.game.console.event;

import com.mccurry.events.core.Event;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class MainThreadConsoleCallbackEvent implements Event {
    private String message;

    public @NonNull String getCallbackMessage() {
        return message;
    }
}
