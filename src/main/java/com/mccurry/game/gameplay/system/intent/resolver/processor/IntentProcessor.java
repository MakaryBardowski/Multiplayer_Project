package com.mccurry.game.gameplay.system.intent.resolver.processor;

import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.system.intent.model.Intent;

public interface IntentProcessor {

    Intent getHandledIntent();
    void processIntent(Entity e);
}
