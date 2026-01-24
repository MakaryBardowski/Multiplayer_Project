package com.mccurry.game.keyBinding.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.keyBinding.model.PlayerAction;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class KeyRebindEvent implements Event {
    private PlayerAction playerAction;
    private String newMapping;
}
