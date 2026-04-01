package com.mccurry.game.gameplay.system.intent.resolver.processor;

import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.system.intent.model.Intent;

public class PrimaryActionProcessor implements IntentProcessor{
    @Override
    public Intent getHandledIntent() {
        return Intent.PRIMARY_ACTION;
    }

    @Override
    public void processIntent(Entity e) {
        System.err.println("Entity (id=%s) intends to perform '%s'".formatted(e.getId(),getHandledIntent()));
    }
}
