package com.mccurry.game.gameplay.system.intent.resolver;

import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.system.intent.model.Intent;
import com.mccurry.game.gameplay.system.intent.resolver.processor.IntentProcessor;
import com.mccurry.game.gameplay.system.intent.resolver.processor.PrimaryActionProcessor;

import java.util.HashMap;
import java.util.Map;

public class IntentResolver {

    private static final Map<Intent, IntentProcessor> PROCESSOR_BY_INTENT_MAP = new HashMap<>();

    static {
        var primaryActionProcessor = new PrimaryActionProcessor();
        PROCESSOR_BY_INTENT_MAP.put(primaryActionProcessor.getHandledIntent(), primaryActionProcessor);
    }

    public void resolve(Intent entityIntent, Entity targetEntity){
        var processor = PROCESSOR_BY_INTENT_MAP.get(entityIntent);
        if(processor == null){
            return;
        }
        processor.processIntent(targetEntity);
    }
}
