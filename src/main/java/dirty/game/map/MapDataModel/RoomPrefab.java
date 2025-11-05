package dirty.game.map.MapDataModel;

import dirty.LevelLoadSystem.entitySpawnData.EntitySpawnData;
import dirty.LevelLoadSystem.entitySpawnData.entityPrototypeData.EntitySpawnDataPrototype;
import dirty.LevelLoadSystem.roomPrefabs.RoomPrefabDeserializer;
import dirty.Utils.Ansi;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jme3.math.Vector3f;
import com.mccurry.map.model.TileMap;
import jme3utilities.math.Vector3i;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@JsonDeserialize(using = RoomPrefabDeserializer.class)
public class RoomPrefab extends TileMap {

    protected List<Vector3i> joinPoints = new ArrayList<>();
    protected List<EntitySpawnData> entitySpawnData = new ArrayList<>();
    protected List<Vector3f> potentialExitSpawnpoints = new ArrayList<>();

    @Setter
    protected Map<Integer,EntitySpawnDataPrototype<EntitySpawnData>> entitySpawnDataPrototypesByResultId = new HashMap<>();

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
    public void addEntitySpawnDataPrototype(EntitySpawnDataPrototype<EntitySpawnData> prototype){
        System.out.println(Ansi.withAnsiColor("adding "+prototype+" by id "+prototype.getLocalResultReferenceId(),Ansi.BRIGHT_YELLOW));
        entitySpawnDataPrototypesByResultId.put(prototype.getLocalResultReferenceId(),prototype);
    }

    public void addPotentialExitSpawnpoint(float x, float y, float z){potentialExitSpawnpoints.add(new Vector3f(x,y,z));}

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


    /**
     *
     * @return deep cloned entity spawn data (can be modified freely)
     */
    public List<Vector3f> getPotentialExitSpawnpoints(){
        var clonedExitPoses = new ArrayList<Vector3f>();
        for(var exitSpawnpoint : potentialExitSpawnpoints){
            clonedExitPoses.add(exitSpawnpoint.clone());
        }
        return clonedExitPoses;
    }
}
