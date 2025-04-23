package LevelLoadSystem.roomPrefabs;

import LevelLoadSystem.LevelLoadResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.map.MapDataModel.RoomPrefab;

import java.io.File;
import java.io.IOException;

public class RoomPrefabLoader {
    private final ObjectMapper mapper = new ObjectMapper();

    public RoomPrefab readRoomPrefab(String filepath) throws IOException {
        return mapper.readValue(new File(filepath), RoomPrefab.class);
    }
}
