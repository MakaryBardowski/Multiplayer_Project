package LevelLoadSystem.roomPrefabs;

import LevelLoadSystem.LevelLoadKeys;
import LevelLoadSystem.LevelLoadResult;
import LevelLoadSystem.entitySpawnData.*;
import LevelLoadSystem.entitySpawnData.entityPrototypeData.EntityPrototypeDataDeserializationUtils;
import Utils.Ansi;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.jme3.math.Vector3f;
import com.mccurry.map.model.Map;

import game.map.MapDataModel.RoomPrefab;
import jme3utilities.math.Vector3i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomPrefabDeserializer extends StdDeserializer<RoomPrefab> {
    protected RoomPrefabDeserializer(){
        super(RoomPrefab.class);
    }

    @Override
    public RoomPrefab deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        var dimensionNode = node.get(LevelLoadKeys.MAP_DIMENSIONS_KEY);

        int mapSizeX = dimensionNode.get(0).asInt();
        int mapSizeY = dimensionNode.get(1).asInt();
        int mapSizeZ = dimensionNode.get(2).asInt();

        byte[] logicMap = new byte[mapSizeX * mapSizeY * mapSizeZ];
        var blocksNode = node.get(LevelLoadKeys.MAP_BLOCK_LAYER_KEY);

        for (var layerData : blocksNode) {
            var height = layerData.get(LevelLoadKeys.MAP_BLOCK_POSITION_Y_KEY).asInt();

            int index = 0;
            for (var blockNode : layerData.get(LevelLoadKeys.MAP_BLOCK_DATA_KEY)) {
                byte blockId = (byte) blockNode.asInt();
                logicMap[Map.positionToIndex(index % mapSizeX, height, index / mapSizeX, mapSizeY, mapSizeZ)] = blockId;
                index++;
            }
        }


        var prefab = new RoomPrefab(logicMap,mapSizeX,mapSizeY,mapSizeZ);
        var joinPoints = node.get(LevelLoadKeys.ROOM_PREFAB_JOIN_POINTS_DATA_KEY);
        for (var joinPoint : joinPoints) {
            var position = joinPoint.get(LevelLoadKeys.ENTITY_POSITION_KEY);
            var x = position.get(LevelLoadKeys.ENTITY_POSITION_X_KEY).asInt();
            var y = position.get(LevelLoadKeys.ENTITY_POSITION_Y_KEY).asInt();
            var z = position.get(LevelLoadKeys.ENTITY_POSITION_Z_KEY).asInt();

            prefab.addJoinPoint(x,y,z);
        }

        var entityPrototypesNode = node.get(LevelLoadKeys.ENTITY_PROTOTYPES_KEY);

        for(var entityPrototypeNode : entityPrototypesNode){
            var selector = EntityPrototypeDataDeserializationUtils.getEntitySpawnDataSelector(entityPrototypeNode);
            prefab.addEntitySpawnDataPrototype(selector);
        }

        var entitesNode = node.get(LevelLoadKeys.ENTITIES_KEY);
        var entitySpawnData = EntitySpawnDataDeserializationUtils.getEntitySpawnDataFromNode(entitesNode,prefab.getEntitySpawnDataPrototypesByResultId());
        for(var esd : entitySpawnData) {
            prefab.addEntitySpawnData(esd);
        }

        var exitSpawnpointsNode = node.get(LevelLoadKeys.LEVEL_EXIT_SPAWNPOINTS_KEY);
        for (var spawnpointNode : exitSpawnpointsNode){
            var positionX = (float) spawnpointNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_X_KEY).asDouble();
            var positionY = (float) spawnpointNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Y_KEY).asDouble();
            var positionZ = (float) spawnpointNode.get(LevelLoadKeys.ENTITY_POSITION_KEY).get(LevelLoadKeys.ENTITY_POSITION_Z_KEY).asDouble();
            prefab.addPotentialExitSpawnpoint(positionX,positionY,positionZ);
        }

        return prefab;
    }
}
