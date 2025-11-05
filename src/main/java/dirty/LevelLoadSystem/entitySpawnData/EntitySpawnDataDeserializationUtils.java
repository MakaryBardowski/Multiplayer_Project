package dirty.LevelLoadSystem.entitySpawnData;

import dirty.LevelLoadSystem.LevelLoadKeys;
import dirty.LevelLoadSystem.LevelLoadValues;
import dirty.LevelLoadSystem.entitySpawnData.entityPrototypeData.EntitySpawnDataPrototype;
import com.fasterxml.jackson.databind.JsonNode;
import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntitySpawnDataDeserializationUtils {

    /**
    * @return returns a list of saved entities data. All item entity data occurs before other entity data.
     */
    public static List<EntitySpawnData> getEntitySpawnDataFromNode(JsonNode entitiesNode, Map<Integer, EntitySpawnDataPrototype<EntitySpawnData>> prototypesByResultId){
        var savedEntitiesData = new ArrayList<EntitySpawnData>();

        for(var entityNode : entitiesNode){
            var entityType = entityNode.get(LevelLoadKeys.ENTITY_TYPE_KEY).asText();
            if(entityType.equals(LevelLoadValues.ENTITY_TYPE_ITEM_VALUE)){
                savedEntitiesData.add(getItemSpawnData(entityNode,prototypesByResultId));
            }
        }

        for(var entityNode : entitiesNode){
            var entityType = entityNode.get(LevelLoadKeys.ENTITY_TYPE_KEY).asText();
            if(entityType.equals(LevelLoadValues.ENTITY_TYPE_DESTRUCTIBLE_DECORATION_VALUE)){
                savedEntitiesData.add(getDestructibleDecorationSpawnData(entityNode,prototypesByResultId));
            } else if(entityType.equals(LevelLoadValues.ENTITY_TYPE_INDESTRUCTIBLE_DECORATION_VALUE)) {
                savedEntitiesData.add(getIndestructibleDecorationSpawnData(entityNode,prototypesByResultId));
            } else if(entityType.equals(LevelLoadValues.ENTITY_TYPE_MOB_VALUE)){
                savedEntitiesData.add(getMobSpawnData(entityNode,prototypesByResultId));
            }
        }

        return savedEntitiesData;
    }

    private static EntitySpawnData getDestructibleDecorationSpawnData(JsonNode entityNode, Map<Integer, EntitySpawnDataPrototype<EntitySpawnData>> prototypesByResultId){
        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();
        var positionX = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_X_KEY).asDouble();
        var positionY = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Y_KEY).asDouble();
        var positionZ = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Z_KEY).asDouble();
        return new DestructibleDecorationSpawnData(entityTemplateIndex,new Vector3f(positionX,positionY,positionZ));
    }

    private static EntitySpawnData getIndestructibleDecorationSpawnData(JsonNode entityNode, Map<Integer, EntitySpawnDataPrototype<EntitySpawnData>> prototypesByResultId){
        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();
        var positionX = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_X_KEY).asDouble();
        var positionY = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Y_KEY).asDouble();
        var positionZ = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Z_KEY).asDouble();
        return new IndestructibleDecorationSpawnData(entityTemplateIndex,new Vector3f(positionX,positionY,positionZ));
    }

    private static EntitySpawnData getItemSpawnData(JsonNode entityNode, Map<Integer, EntitySpawnDataPrototype<EntitySpawnData>> prototypesByResultId){
        var fromPrototypeNode = entityNode.get(LevelLoadKeys.ENTITY_FROM_PROTOTYPE_WITH_ID_KEY);

        var positionX = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_X_KEY).asDouble();
        var positionY = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Y_KEY).asDouble();
        var positionZ = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Z_KEY).asDouble();
        var position = new Vector3f(positionX,positionY,positionZ);
        if(fromPrototypeNode != null && fromPrototypeNode.isNumber()){
            return new PrototypeEntitySpawnData(prototypesByResultId.get(fromPrototypeNode.asInt()), position);
        }


        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();
        var isDropped = entityNode.get(LevelLoadKeys.ENTITY_ITEM_IS_DROPPED).asBoolean();
        var droppable = entityNode.get(LevelLoadKeys.ENTITY_ITEM_IS_DROPPABLE).asBoolean();

        return new ItemSpawnData(entityTemplateIndex,position,isDropped,droppable);
    }

    private static EntitySpawnData getMobSpawnData(JsonNode entityNode, Map<Integer, EntitySpawnDataPrototype<EntitySpawnData>> prototypesByResultId){
        var fromPrototypeNode = entityNode.get(LevelLoadKeys.ENTITY_FROM_PROTOTYPE_WITH_ID_KEY);

        var positionX = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_X_KEY).asDouble();
        var positionY = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Y_KEY).asDouble();
        var positionZ = (float) entityNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Z_KEY).asDouble();
        var position = new Vector3f(positionX,positionY,positionZ);

        if(fromPrototypeNode != null && fromPrototypeNode.isNumber()){
            return new PrototypeEntitySpawnData(prototypesByResultId.get(fromPrototypeNode.asInt()), position);
        }

        var entityTemplateIndex = entityNode.get(LevelLoadKeys.ENTITY_TEMPLATE_INDEX_KEY).asInt();

        return new MobSpawnData(entityTemplateIndex,position);
    }
}
