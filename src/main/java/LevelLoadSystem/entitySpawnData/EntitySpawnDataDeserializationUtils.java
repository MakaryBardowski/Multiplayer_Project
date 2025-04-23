package LevelLoadSystem.entitySpawnData;

import LevelLoadSystem.LevelLoadKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class EntitySpawnDataDeserializationUtils {

    /**
    * @return returns a list of saved entities data. All item entity data occurs before other entity data.
     */
    public static List<EntitySpawnData> getEntitySpawnDataFromNode(JsonNode entitiesNode){
        var savedEntitiesData = new ArrayList<EntitySpawnData>();

        for(var entityNode : entitiesNode){
            var entityTypeIndex = entityNode.get(LevelLoadKeys.ENTITY_TYPE_INDEX_KEY).asInt();
            if(entityTypeIndex == 1){
                savedEntitiesData.add(getItemSpawnData(entityNode));
            }
        }

        for(var entityNode : entitiesNode){
            var entityTypeIndex = entityNode.get(LevelLoadKeys.ENTITY_TYPE_INDEX_KEY).asInt();
            if(entityTypeIndex == 0){
                savedEntitiesData.add(getDestructibleDecorationSpawnData(entityNode));
            } else if(entityTypeIndex == 2) {
                savedEntitiesData.add(getIndestructibleDecorationSpawnData(entityNode));
            } else if(entityTypeIndex == 3){
                savedEntitiesData.add(getMobSpawnData(entityNode));
            }
        }

        return savedEntitiesData;
    }

    private static EntitySpawnData getDestructibleDecorationSpawnData(JsonNode entityNode){
        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();
        var positionX = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_X_KEY).asDouble();
        var positionY = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Y_KEY).asDouble();
        var positionZ = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Z_KEY).asDouble();
        return new DestructibleDecorationSpawnData(entityTemplateIndex,new Vector3f(positionX,positionY,positionZ));
    }

    private static EntitySpawnData getIndestructibleDecorationSpawnData(JsonNode entityNode){
        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();
        var positionX = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_X_KEY).asDouble();
        var positionY = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Y_KEY).asDouble();
        var positionZ = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Z_KEY).asDouble();
        return new IndestructibleDecorationSpawnData(entityTemplateIndex,new Vector3f(positionX,positionY,positionZ));
    }

    private static EntitySpawnData getItemSpawnData(JsonNode entityNode){
        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();
        var positionX = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_X_KEY).asDouble();
        var positionY = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Y_KEY).asDouble();
        var positionZ = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Z_KEY).asDouble();
        var isDropped = entityNode.get(LevelLoadKeys.ENTITY_ITEM_IS_DROPPED).asBoolean();
        var droppable = entityNode.get(LevelLoadKeys.ENTITY_ITEM_IS_DROPPABLE).asBoolean();

        return new ItemSpawnData(entityTemplateIndex,new Vector3f(positionX,positionY,positionZ),isDropped,droppable);
    }

    private static EntitySpawnData getMobSpawnData(JsonNode entityNode){
        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();
        var positionX = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_X_KEY).asDouble();
        var positionY = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Y_KEY).asDouble();
        var positionZ = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Z_KEY).asDouble();

        return new MobSpawnData(entityTemplateIndex,new Vector3f(positionX,positionY,positionZ));
    }
}
