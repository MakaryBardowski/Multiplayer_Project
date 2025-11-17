package com.mccurry.game.shared.ui.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.shared.settings.model.UserSettings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MainThreadRefreshUiEvent implements Event {
    private final UserSettings userSettingsCopy;
}
