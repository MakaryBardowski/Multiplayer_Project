package com.mccurry.game.gameplay.system.intent.event;

import com.mccurry.game.gameplay.core.ecs.entity.Entity;

public class SecondaryActionIntentEvent extends IntentEvent{
    public SecondaryActionIntentEvent(Entity targetEntity) {
        super(targetEntity);
    }
}
