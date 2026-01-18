package com.mccurry.game.shared.ui.event;

import com.mccurry.events.core.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MainThreadResizeUiEvent implements Event {
    private final int uiScale;
}
