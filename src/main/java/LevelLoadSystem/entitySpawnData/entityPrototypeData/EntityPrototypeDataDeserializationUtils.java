package LevelLoadSystem.entitySpawnData.entityPrototypeData;

import LevelLoadSystem.LevelLoadKeys;
import LevelLoadSystem.LevelLoadValues;
import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import LevelLoadSystem.entitySpawnData.EntitySpawnDataFromPrototypeDeserializationUtils;
import com.fasterxml.jackson.databind.JsonNode;
import data.ChanceEntry;
import generators.PercentageRandomGenerator;

import java.util.ArrayList;
import java.util.List;

public class EntityPrototypeDataDeserializationUtils {

    public static EntitySpawnDataSelector<EntitySpawnData> getEntitySpawnDataSelector(JsonNode entityPrototypeNode){
        var entityPrototypeArchetype = entityPrototypeNode.get(LevelLoadKeys.ENTITY_PROTOTYPE_TYPE_KEY).asText();
        var resultReferenceId = entityPrototypeNode.get(LevelLoadKeys.ENTITY_PROTOTYPE_REFERENCE_ID_KEY).asInt();

        if(entityPrototypeArchetype.equals(LevelLoadValues.ENTITY_PROTOTYPE_TYPE_SELECTOR_VALUE)){
            var selectorType = entityPrototypeNode.get(LevelLoadKeys.SELECTOR_ENTITY_PROTOTYPE_TYPE_KEY).asText();
//            var resolvedType = entityPrototypeNode.get(LevelLoadKeys.SELECTOR_ENTITY_PROTOTYPE_RESOLVED_TYPE_KEY).asText();

            if(selectorType.equals(LevelLoadValues.SELECTOR_RANDOM_BY_PERCENT_VALUE)){
                List<ChanceEntry<EntitySpawnData>> results = new ArrayList<>();
                var chanceEntriesNode = entityPrototypeNode.get(LevelLoadKeys.SELECTOR_ENTITY_PROTOTYPE_CHANCE_ENTRIES_KEY);
                float percentChanceSum = 0;
                for(var chanceEntryNode : chanceEntriesNode){
                    var chanceEntry = getEntitySpawnDataChanceEntry(chanceEntryNode);
                    results.add(chanceEntry);
                    percentChanceSum += chanceEntry.getChance();
                }

                if(percentChanceSum < 100){
                    var chanceForNoResult = 100 - percentChanceSum;
                    results.add(new ChanceEntry<EntitySpawnData>(chanceForNoResult,null));
                }

                var randomGen = new PercentageRandomGenerator<>(results);
                return new RandomEntitySpawnDataSelector<>(resultReferenceId,randomGen);
            } else {
                throw new IllegalArgumentException("\""+selectorType+"\" selector type not supported.");
            }
        }
        throw new IllegalArgumentException("Only entity prototype \"type\" supported are: ["+LevelLoadValues.ENTITY_PROTOTYPE_TYPE_SELECTOR_VALUE+"]");
    }

    public static ChanceEntry<EntitySpawnData> getEntitySpawnDataChanceEntry(JsonNode chanceEntryNode){
        var resolvedObjectNode = chanceEntryNode.get(LevelLoadKeys.SELECTOR_ENTITY_PROTOTYPE_RESOLVED_OBJECT_KEY);
        var resolvedEntitySpawnData = EntitySpawnDataFromPrototypeDeserializationUtils.getResolvedObjectEntitySpawnData(resolvedObjectNode);
        var percentChance =  (float) chanceEntryNode.get(LevelLoadKeys.SELECTOR_ENTITY_PROTOTYPE_PERCENT_CHANCE_KEY).asDouble();
        return new ChanceEntry<>(percentChance,resolvedEntitySpawnData);
    }

}
