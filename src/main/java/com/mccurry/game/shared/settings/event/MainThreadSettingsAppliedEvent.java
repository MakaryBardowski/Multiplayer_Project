package com.mccurry.game.shared.settings.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.shared.settings.model.UserSettings;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MainThreadSettingsAppliedEvent implements Event {
    private UserSettings newSettings;
}
