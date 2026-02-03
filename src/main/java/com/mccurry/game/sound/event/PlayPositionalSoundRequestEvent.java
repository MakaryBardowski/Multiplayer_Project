package com.mccurry.game.sound.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.sound.model.positional.PositionalSoundEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayPositionalSoundRequestEvent implements Event {
    private PositionalSoundEntry soundEntry;
    private int x;
    private int y;
    private int z;
}
