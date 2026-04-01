package com.mccurry.game.gameplay.system.intent.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.system.intent.model.Intent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IntentEvent implements Event {
    private final Intent intent;
    private final Entity targetEntity;
}
