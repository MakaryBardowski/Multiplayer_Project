package com.mccurry.game.shared.ui.transition.events;

import com.mccurry.events.core.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StartScreenFadeOutEvent implements Event {
    private final float fadeOutTime;
}
