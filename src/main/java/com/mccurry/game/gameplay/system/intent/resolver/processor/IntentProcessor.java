package com.mccurry.game.gameplay.system.intent.resolver.processor;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.intent.model.Intent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class IntentProcessor {
    protected final EventBus eventBus;
    protected final EntityStore entityStore;

    public abstract Intent getHandledIntent();
    public abstract void processIntent(Entity e);
}
