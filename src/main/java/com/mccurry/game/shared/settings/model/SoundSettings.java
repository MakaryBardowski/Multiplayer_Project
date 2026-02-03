package com.mccurry.game.shared.settings.model;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SoundSettings {
    private Integer masterVolume;
    private Integer musicVolume;
    private Integer sfxVolume;
    private Integer gunshotVolume;
}
