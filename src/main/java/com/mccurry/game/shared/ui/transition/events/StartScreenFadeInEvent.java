package com.mccurry.game.shared.ui.transition.events;

import com.mccurry.events.core.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public class StartScreenFadeInEvent implements Event {
    private final float fadeInTime;
    @Nullable private final Runnable onFadeInFinished;
}
