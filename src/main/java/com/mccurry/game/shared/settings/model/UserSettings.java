package com.mccurry.game.shared.settings.model;

import com.mccurry.game.gameplayConfig.keyBinding.model.KeyBind;
import com.mccurry.game.gameplayConfig.keyBinding.model.PlayerAction;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;

import static com.mccurry.game.gameplayConfig.keyBinding.KeyBindingRegistry.getNewDefaultBindings;

@Getter
@Setter
@Accessors(chain = true)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSettings {

    //todo: do not use primitives! (overriding issues pop up, as jackson overrides when fields are not null, and primitives cant be null = always override)
    private ResolutionSettings resolutionSetting;
    private Boolean fullscreen;
    private Boolean vSync;
    private Integer maxFps;
    private LinkedHashMap<PlayerAction, KeyBind> keyBinds;

    public UserSettings deepClone() {

        var clonedBinds = new LinkedHashMap<PlayerAction, KeyBind>();
        keyBinds.forEach(
                (key, value) -> clonedBinds.put(
                        key,
                        value.toBuilder().build()
                )
        );

        return UserSettings.builder()
                .resolutionSetting(resolutionSetting)
                .fullscreen(fullscreen)
                .vSync(vSync)
                .maxFps(maxFps)
                .keyBinds(clonedBinds)
                .build();
    }


    public static UserSettings getDefault() {
        var settings = new UserSettings();
        return settings
                .setResolutionSetting(ResolutionSettings.R_1280x720)
                .setFullscreen(false)
                .setVSync(true)
                .setMaxFps(120)
                .setKeyBinds(getNewDefaultBindings());
    }
}
