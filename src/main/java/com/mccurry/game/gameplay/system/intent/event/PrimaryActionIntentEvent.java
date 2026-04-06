package com.mccurry.game.gameplay.system.intent.event;

import com.mccurry.game.gameplay.core.ecs.entity.Entity;

public class PrimaryActionIntentEvent extends IntentEvent{
    public PrimaryActionIntentEvent(Entity targetEntity) {
        super(targetEntity);
    }
}
