package com.mccurry.game.console.event;

import com.mccurry.events.core.Event;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class MainThreadConsoleStateResponseEvent implements Event {
    private String uiState;

    public @NonNull String getStringState() {
        return uiState;
    }
}
