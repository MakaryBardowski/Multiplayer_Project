package dirty.game.map.MapDataModel;

import dirty.LevelLoadSystem.roomPrefabs.RoomPrefabLoader;
import dirty.client.Main;
import dirty.game.map.AxisAlignedRotation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Files.walk;

public class RoomPrefabs {
    private static final String PREFABS_PWD = Main.isIDEMode() ? "assets/RoomPrefabs/" : Paths.get("").toAbsolutePath() + "/RoomPrefabs/";
    private static final String ROOM_PREFAB_FILE_EXTENSION = ".json";
    private static final List<RotatedRoomPrefabs> PREFABS = new ArrayList<>();

    static {
        Path prefabsDir = Paths.get(PREFABS_PWD);
        var prefabLoader = new RoomPrefabLoader();
        try (Stream<Path> filePaths = walk(prefabsDir)) {
            filePaths.forEach(path -> {
                if (path.toString().endsWith(ROOM_PREFAB_FILE_EXTENSION)) {
                    try {
                        var roomPrefab = prefabLoader.readRoomPrefab(path.toString());
                        PREFABS.add(new RotatedRoomPrefabs(roomPrefab));
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read room prefab: " + path + " error: "+e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to access room prefab directory. "+e);
        }
    }

    public static RoomPrefab getPrefabAtIndex(int index, AxisAlignedRotation rotation) {
        return PREFABS.get(index).get(rotation);
    }
}
