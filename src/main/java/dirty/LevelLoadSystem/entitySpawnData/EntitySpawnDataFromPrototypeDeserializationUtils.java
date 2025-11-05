package dirty.LevelLoadSystem.entitySpawnData;

import dirty.LevelLoadSystem.LevelLoadKeys;
import dirty.LevelLoadSystem.LevelLoadValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.jme3.math.Vector3f;

public class EntitySpawnDataFromPrototypeDeserializationUtils {


    public static EntitySpawnData getResolvedObjectEntitySpawnData(JsonNode resolvedObjectNode) {
        if(resolvedObjectNode.asText() .equals( "null" )){
            return null;
        }
        var entityType = resolvedObjectNode.get(LevelLoadKeys.ENTITY_TYPE_KEY).asText();

        if(entityType.equals(LevelLoadValues.ENTITY_TYPE_ITEM_VALUE)){
          return getItemSpawnData(resolvedObjectNode);
        } else if (entityType.equals(LevelLoadValues.ENTITY_TYPE_DESTRUCTIBLE_DECORATION_VALUE)) {
            return getDestructibleDecorationSpawnData(resolvedObjectNode);
        } else if (entityType.equals(LevelLoadValues.ENTITY_TYPE_INDESTRUCTIBLE_DECORATION_VALUE)) {
            return getIndestructibleDecorationSpawnData(resolvedObjectNode);
        } else if (entityType.equals(LevelLoadValues.ENTITY_TYPE_MOB_VALUE)) {
            return getMobSpawnData(resolvedObjectNode);
        }

        return null;
    }

    private static EntitySpawnData getDestructibleDecorationSpawnData(JsonNode entityNode) {
        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();
        return new DestructibleDecorationSpawnData(entityTemplateIndex, new Vector3f());
    }

    private static EntitySpawnData getIndestructibleDecorationSpawnData(JsonNode entityNode) {
        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();
        return new IndestructibleDecorationSpawnData(entityTemplateIndex, new Vector3f());
    }

    private static EntitySpawnData getItemSpawnData(JsonNode entityNode) {
        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();
        var isDropped = entityNode.get(LevelLoadKeys.ENTITY_ITEM_IS_DROPPED).asBoolean();
        var droppable = entityNode.get(LevelLoadKeys.ENTITY_ITEM_IS_DROPPABLE).asBoolean();

        return new ItemSpawnData(entityTemplateIndex, new Vector3f(), isDropped, droppable);
    }

    private static EntitySpawnData getMobSpawnData(JsonNode entityNode) {
        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();

        return new MobSpawnData(entityTemplateIndex, new Vector3f());
    }
}
