package com.mccurry.game.shared.settings.model;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSettings {
    public static UserSettings getDefault(){
       var settings = new UserSettings();
       return settings
               .setResolutionSetting(ResolutionSettings.R_1280x720)
               .setFullscreen(false)
               .setVSync(true)
               .setMaxFps(120)
               ;
    }

    //todo: do not use primitives! (overriding issues pop up, as jackson overrides when fields are not null, and primitives cant be null = always override)
    private ResolutionSettings resolutionSetting;
    private Boolean fullscreen;
    private Boolean vSync;
    private Integer maxFps;

}
