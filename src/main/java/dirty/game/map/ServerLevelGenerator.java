package dirty.game.map;

import dirty.LevelLoadSystem.LevelLoader;
import dirty.client.Main;
import com.jme3.math.Vector3f;
import dirty.game.map.proceduralGeneration.BspMapGenerator;
import dirty.game.map.proceduralGeneration.CellularAutomataMapGenerator;
import dirty.game.map.proceduralGeneration.RandomMapGenerator;
import dirty.game.map.proceduralGeneration.stages.Stage1MapGenerator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class ServerLevelGenerator {
    private static final String MAPS_PWD = Main.isIDEMode() ? "assets/Maps/" :  Paths.get("").toAbsolutePath().toString()+"/Maps/";
    private static final String LEVEL_FILE_EXTENSION = ".json";
    private static final String INVALID_MAP_TYPE_FOR_FILE_LOADING_PROVIDED_MESSAGE = "cannot load map of given type '%s'. Maps can be loaded with types ARMORY,BOSS";

    public static final String SAVED_MAP_FILEPATH_TEMPLATE = MAPS_PWD + "Map";


    private final long levelSeed;
    private final MapType mapType;
    private final int levelIndex;

    public ServerLevelGenerator(long levelSeed, MapType mapType, int levelIndex){
        this.levelSeed = levelSeed;
        this.mapType = mapType;
        this.levelIndex = levelIndex;
    }

    public LevelGenerationResult generateLevel(int generatedMapSizeX, int generatedMapSizeY, int generatedMapSizeZ) throws IOException {
        switch (mapType) {
            case FILE: {
//                var levelFilePath = getSavedLevelFilepath(mapType, levelIndex);
//                var levelLoadResult = new LevelLoader().readLevelFile(levelFilePath);
//                var map = levelLoadResult.getMap();
//                var entitySpawnData  = levelLoadResult.getSavedEntityData();
//                return new LevelGenerationResult(map,entitySpawnData,levelLoadResult.getPlayerSpawnpoints());
                var mapGenResult = new Stage1MapGenerator(levelSeed, generatedMapSizeX,generatedMapSizeY,generatedMapSizeZ).createRandomMap();
                var room = mapGenResult.getRooms().get(0);
                return new LevelGenerationResult(mapGenResult.getMap(), mapGenResult.getEntitySpawnData(),
                        List.of(new Vector3f(room.getStartX()+5,room.getStartY()+1,room.getStartZ()+5))
                        );
            }
            case NAIVE: {
                var mapGenResult = new RandomMapGenerator(levelSeed, generatedMapSizeX,generatedMapSizeY,generatedMapSizeZ).createRandomMap();
                var entitySpawnDataGenerator = new EntitySpawnDataGenerator(levelSeed);
                var entitySpawnData = entitySpawnDataGenerator.generateEntitySpawnData(mapGenResult);
                var playerSpawnpoints = entitySpawnDataGenerator.generatePlayerSpawnpoints(mapGenResult);
                return new LevelGenerationResult(mapGenResult.getMap(),entitySpawnData, playerSpawnpoints);
            }
            case BSP: {
                var mapGenResult = new BspMapGenerator(levelSeed, generatedMapSizeX,generatedMapSizeY,generatedMapSizeZ).createRandomMap();
                var entitySpawnDataGenerator = new EntitySpawnDataGenerator(levelSeed);
                var entitySpawnData = entitySpawnDataGenerator.generateEntitySpawnData(mapGenResult);
                var playerSpawnpoints = entitySpawnDataGenerator.generatePlayerSpawnpoints(mapGenResult);
                return new LevelGenerationResult(mapGenResult.getMap(),entitySpawnData, playerSpawnpoints);
            }
            case CELLULAR_AUTOMATA: {
                var mapGenResult = new CellularAutomataMapGenerator(levelSeed, generatedMapSizeX,generatedMapSizeY,generatedMapSizeZ).createRandomMap();
                var entitySpawnDataGenerator = new EntitySpawnDataGenerator(levelSeed);
                var entitySpawnData = entitySpawnDataGenerator.generateEntitySpawnData(mapGenResult);
                var playerSpawnpoints = entitySpawnDataGenerator.generatePlayerSpawnpoints(mapGenResult);
                return new LevelGenerationResult(mapGenResult.getMap(),entitySpawnData, playerSpawnpoints);
            }
            default: {
                var levelFilePath = getSavedLevelFilepath(mapType, levelIndex);
                var levelLoadResult = new LevelLoader().readLevelFile(levelFilePath);
                var map = levelLoadResult.getMap();
                var entitySpawnData  = levelLoadResult.getSavedEntityData();
                return new LevelGenerationResult(map,entitySpawnData,levelLoadResult.getPlayerSpawnpoints());
            }
        }
    }


    public String getSavedLevelFilepath(MapType mapType, int levelIndex){
        if(mapType == MapType.FILE) {
            return SAVED_MAP_FILEPATH_TEMPLATE+levelIndex+LEVEL_FILE_EXTENSION;
        }
        throw new IllegalArgumentException(String.format(INVALID_MAP_TYPE_FOR_FILE_LOADING_PROVIDED_MESSAGE,mapType));
    }

}
