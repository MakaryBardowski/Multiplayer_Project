package com.mccurry.game.shared.ui.service;

import com.mccurry.game.shared.settings.model.ResolutionSettings;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;

import java.util.HashMap;
import java.util.Map;

public class UiResolutionSettingsService {
    private static final ResolutionSettings BASE_UI_RESOLUTION = ResolutionSettings.R_640x360;

    private static final Map<ResolutionSettings, Integer> RESOLUTION_SETTINGS_TO_UI_SCALE = new HashMap<>(){{
        put(ResolutionSettings.R_640x360,1);
        put(ResolutionSettings.R_1280x720,2);
        put(ResolutionSettings.R_1600x900,2);
        put(ResolutionSettings.R_1920x1080,3);
        put(ResolutionSettings.R_2560x1440,4);
        put(ResolutionSettings.R_3840x2160,6);

    }};

    public UiResolutionSettings getFor(UserSettings userSettings){
        var resolutionSettings = userSettings.getResolutionSetting();
        return UiResolutionSettings.builder()
                .width(resolutionSettings.getWidth())
                .height(resolutionSettings.getHeight())
                .uiScale(getUiScaleFor(resolutionSettings))
                .build();
    }

    private int getUiScaleFor(ResolutionSettings resolutionSetting){
        return RESOLUTION_SETTINGS_TO_UI_SCALE.getOrDefault(resolutionSetting,1);
    }
}
