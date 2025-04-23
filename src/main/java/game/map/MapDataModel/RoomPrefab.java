package game.map.MapDataModel;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import LevelLoadSystem.roomPrefabs.RoomPrefabDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jme3utilities.math.Vector3i;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonDeserialize(using = RoomPrefabDeserializer.class)
public class RoomPrefab extends TileMap{

    protected List<Vector3i> joinPoints = new ArrayList<>();
    protected List<EntitySpawnData> entitySpawnData = new ArrayList<>();

    public RoomPrefab(byte[] logicMap, int mapSizeX, int mapSizeY, int mapSizeZ) {
        super(logicMap, mapSizeX, mapSizeY, mapSizeZ);
    }

    // coordinates in logicMap
    public void addJoinPoint(int x, int y, int z){
        joinPoints.add(new Vector3i(x,y,z));
    }
    public void addEntitySpawnData(EntitySpawnData esd){
        entitySpawnData.add(esd);
    }

    /**
     *
     * @return deep cloned entity spawn data (can be modified freely)
     */
    public List<EntitySpawnData> getEntitySpawnData(){
        var clonedEsd = new ArrayList<EntitySpawnData>();
        for(var originalEsd : entitySpawnData){
            clonedEsd.add(originalEsd.clone());
        }
        return clonedEsd;
    }
}
