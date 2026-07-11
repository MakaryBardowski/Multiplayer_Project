package com.mccurry.game.gameplay.system.intent.resolver;

import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.intent.event.IntentEvent;
import com.mccurry.game.gameplay.system.intent.resolver.model.IntentSystems;
import com.mccurry.game.gameplay.system.intent.resolver.processor.HandEquipActionProcessor;
import com.mccurry.game.gameplay.system.intent.resolver.processor.IntentProcessor;
import com.mccurry.game.gameplay.system.intent.resolver.processor.PrimaryActionProcessor;
import com.mccurry.game.gameplay.system.intent.resolver.processor.SecondaryActionProcessor;

import java.util.HashMap;
import java.util.Map;


public class IntentResolver {

    private final Map<Class<? extends IntentEvent>, IntentProcessor> processorByIntentClassMap = new HashMap<>();

    public IntentResolver(IntentSystems intentSystems, EntityStore entityStore){
        var primaryActionProcessor = new PrimaryActionProcessor(entityStore, intentSystems.getAttackSystem());
        var secondaryActionProcessor = new SecondaryActionProcessor(entityStore, intentSystems.getAttackSystem());
        var handEquipActionProcessor = new HandEquipActionProcessor(entityStore, intentSystems.getEquipSystem());
        processorByIntentClassMap.put(primaryActionProcessor.getHandledIntentEvent(), primaryActionProcessor);
        processorByIntentClassMap.put(secondaryActionProcessor.getHandledIntentEvent(), secondaryActionProcessor);
        processorByIntentClassMap.put(handEquipActionProcessor.getHandledIntentEvent(), handEquipActionProcessor);
    }

    public void resolve(IntentEvent intentEvent, float tpf, double gameTimeSeconds){
        var processor = processorByIntentClassMap.get(intentEvent.getClass());
        if(processor == null){
            return;
        }
        processor.processIntent(intentEvent, tpf, gameTimeSeconds);
    }
}
