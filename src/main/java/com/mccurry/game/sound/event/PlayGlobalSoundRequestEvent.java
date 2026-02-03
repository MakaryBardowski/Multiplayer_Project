package com.mccurry.game.sound.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.sound.model.global.GlobalSoundEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayGlobalSoundRequestEvent implements Event {
    private GlobalSoundEntry soundEntry;
}
