package com.mccurry.game.locale.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.locale.model.GameLocale;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MainThreadLocaleChangedEvent implements Event {
    private final GameLocale newLocale;
}
