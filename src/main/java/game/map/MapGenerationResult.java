package game.map;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import com.mccurry.map.model.Map;

import game.map.proceduralGeneration.Room;

import java.util.List;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
public class MapGenerationResult {
    private Map map;
    private List<Room> rooms; //todo:  remove

    private List<EntitySpawnData> entitySpawnData;

    public MapGenerationResult(Map map, List<Room> rooms){
       this(map,rooms,null);
    }

    public MapGenerationResult(Map map, List<Room> rooms, List<EntitySpawnData> entitySpawnData){
        this.map = map;
        this.rooms = rooms;
        this.entitySpawnData = entitySpawnData;
    }
}
