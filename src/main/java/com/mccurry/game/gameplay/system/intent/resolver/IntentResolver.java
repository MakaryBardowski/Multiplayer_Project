package com.mccurry.game.gameplay.system.intent.resolver;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.intent.model.Intent;
import com.mccurry.game.gameplay.system.intent.resolver.processor.IntentProcessor;
import com.mccurry.game.gameplay.system.intent.resolver.processor.PrimaryActionProcessor;

import java.util.HashMap;
import java.util.Map;


public class IntentResolver {

    private final Map<Intent, IntentProcessor> processorByIntentMap = new HashMap<>();

    public IntentResolver(EventBus eventBus, EntityStore entityStore){
        var primaryActionProcessor = new PrimaryActionProcessor(eventBus,entityStore);
        processorByIntentMap.put(primaryActionProcessor.getHandledIntent(), primaryActionProcessor);
    }

    public void resolve(Intent entityIntent, Entity targetEntity){
        var processor = processorByIntentMap.get(entityIntent);
        if(processor == null){
            return;
        }
        processor.processIntent(targetEntity);
    }
}
