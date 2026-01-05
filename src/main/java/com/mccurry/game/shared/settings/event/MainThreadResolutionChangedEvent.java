package com.mccurry.game.shared.settings.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.shared.settings.model.ResolutionSettings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MainThreadResolutionChangedEvent implements Event {
    private final ResolutionSettings resolutionSetting;
}
