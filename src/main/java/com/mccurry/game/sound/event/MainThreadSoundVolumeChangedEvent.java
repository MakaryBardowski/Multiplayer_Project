package com.mccurry.game.sound.event;

import com.mccurry.events.core.Event;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MainThreadSoundVolumeChangedEvent implements Event {
    private int masterVolumePercentage;
    private int musicVolumePercentage;
    private int sfxVolumePercentage;
    private int gunshotVolumePercentage;
}
