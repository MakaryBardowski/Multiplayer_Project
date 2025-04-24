package game.map.proceduralGeneration.stages;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import client.appStates.ClientGameAppState;
import data.ChanceEntry;
import game.map.AxisAlignedRotation;
import game.map.MapDataModel.PlacedRoomPrefab;
import game.map.MapDataModel.RoomPrefabs;
import game.map.MapGenerationResult;
import game.map.MapDataModel.Map;
import game.map.blocks.BlockType;
import game.map.proceduralGeneration.MapGenerator;
import game.map.proceduralGeneration.Room;
import generators.PercentageRandomGenerator;
import jme3utilities.math.Vector3i;

import java.util.*;

public class Stage1MapGenerator extends MapGenerator {
    private final LinkedHashSet<Vector3i> openJoinPoints = new LinkedHashSet<>();
    private final List<PlacedRoomPrefab> alreadyPlacedPrefabs = new ArrayList<>();

    private final PercentageRandomGenerator<Integer> prefabIndexGenerator = new PercentageRandomGenerator<>(List.of(
            new ChanceEntry<>(20f,1),
            new ChanceEntry<>(40f,2),
            new ChanceEntry<>(10f,3),
            new ChanceEntry<>(10f,4),
            new ChanceEntry<>(20f,5)
    ));

    public Stage1MapGenerator(long seed, int mapSizeX, int mapSizeY, int mapSizeZ) {
        super(seed, mapSizeX, mapSizeY, mapSizeZ);
    }

    @Override
    public MapGenerationResult createRandomMap() {
        byte[] flatMap = new byte[mapSizeX * mapSizeY * mapSizeZ];
        Arrays.fill(flatMap, (byte) 0);
        Map logicMap = new Map(flatMap, mapSizeX, mapSizeY, mapSizeZ, ClientGameAppState.getInstance().getBLOCK_SIZE());


        var playerRoom = RoomPrefabs.getPrefabAtIndex(0, AxisAlignedRotation.FACE_SOUTH_MINUS_Z_AXIS);
        var playerRoomPosX = mapSizeX / 2;
        var playerRoomPosY = 0;
        var playerRoomPosZ = mapSizeZ / 2;

        var isPlayerRoomSuccessfullyPlaced = logicMap.placePrefabIfPossible(playerRoomPosX, playerRoomPosY, playerRoomPosZ, playerRoom, alreadyPlacedPrefabs);

        if (isPlayerRoomSuccessfullyPlaced) {
            for (var joinPoint : playerRoom.getJoinPoints()) {
                openJoinPoints.add(new Vector3i(playerRoomPosX + joinPoint.x(), playerRoomPosY + joinPoint.y(), playerRoomPosZ + joinPoint.z()));

//                logicMap.setBlockIdAtPosition(playerRoomPosX + joinPoint.x(), playerRoomPosY + joinPoint.y(), playerRoomPosZ + joinPoint.z(), BlockType.WATER.blockId);
            }
            alreadyPlacedPrefabs.add(new PlacedRoomPrefab(playerRoom,playerRoomPosX,playerRoomPosY,playerRoomPosZ));
        }

        int numRoomTrials = 300; // 300;
        for (int i = 0; i < numRoomTrials && !openJoinPoints.isEmpty(); i++) {
            for (int roomTryNum = 0; roomTryNum < 3; roomTryNum++) {
                var numberRotations = AxisAlignedRotation.values().length; // north,east west south
                var rotationIndex = random.nextInt(numberRotations);
                var testedPrefabIndex = prefabIndexGenerator.getRandom();
                var pointToConnectNewPrefabTo = getRandomJoinPoint(openJoinPoints);

                var joinPointNum = RoomPrefabs.getPrefabAtIndex(testedPrefabIndex, AxisAlignedRotation.FACE_NORTH_PLUS_Z_AXIS).getJoinPoints().size();
                var joinPointIndex = random.nextInt(joinPointNum);
                if (tryToPlaceRoom(logicMap,pointToConnectNewPrefabTo,rotationIndex,numberRotations,testedPrefabIndex,joinPointIndex)) {
                    break;
                }
            }
        }


        for(var remainingJoinPoint : openJoinPoints){
            var joinPointX = remainingJoinPoint.x();
            var joinPointY = remainingJoinPoint.y();
            var joinPointZ = remainingJoinPoint.z();

            if(logicMap.isPositionNotEmpty(joinPointX,joinPointY,joinPointZ)){
                logicMap.setBlockIdAtPosition(joinPointX,joinPointY,joinPointZ,BlockType.OFFICE_FLOOR.blockId); // air
            }else {
                logicMap.setBlockIdAtPosition(joinPointX, joinPointY, joinPointZ, BlockType.WATER.blockId); // some block
            }
        }

        // get entitySpawnData
        var entitySpawnData = new ArrayList<EntitySpawnData>();
        for(var placedRoom : alreadyPlacedPrefabs){
            var esd = placedRoom.getEntitySpawnData();
            for(var clonedEsd : esd){
                clonedEsd.getPosition().addLocal(placedRoom.getPosX(),placedRoom.getPosY(),placedRoom.getPosZ());
            }
            entitySpawnData.addAll(esd);
        }

        return new MapGenerationResult(
                logicMap,
                List.of(new Room(mapSizeX / 2, 0, mapSizeZ / 2, mapSizeX / 2 + 8, 1, mapSizeZ / 2 + 8)),
                entitySpawnData
                );
    }

    private Vector3i getRandomJoinPoint(Set<Vector3i> joinPoints){
        var length = joinPoints.size();
        var i = 0;
        var randomIndex = random.nextInt(length);
        for(var joinPoint : joinPoints){
            if(i == randomIndex){
                return joinPoint;
            }
            i++;
        }
        return null;
    }




    /**
     * @return true if room successfully placed, false if not
     */
    private boolean tryToPlaceRoom(Map logicMap,Vector3i pointToConnectNewPrefabTo, int rotationIndex, int numberRotations, int testedPrefabIndex, int joinPointIndex){
        for (int triedRotationNumber = 0; triedRotationNumber < numberRotations; triedRotationNumber++) {

            rotationIndex++; // "rolling" +1 to rotation ordinal
            if (rotationIndex >= numberRotations) {
                rotationIndex = 0;
            }
            var randomRotation = AxisAlignedRotation.values()[rotationIndex];


            // try to place room

            var testedPrefab = RoomPrefabs.getPrefabAtIndex(testedPrefabIndex, randomRotation);
            var testedPrefabJoinPoints = testedPrefab.getJoinPoints();
            var testedPrefabRandomJoinPoint = testedPrefabJoinPoints.get(
                    joinPointIndex
            );

            var testedPrefabPosX = pointToConnectNewPrefabTo.x() - testedPrefabRandomJoinPoint.x();
            var testedPrefabPosY = 0;
            var testedPrefabPosZ = pointToConnectNewPrefabTo.z() - testedPrefabRandomJoinPoint.z();

            var successPlacing = logicMap.placePrefabIfPossible(testedPrefabPosX, testedPrefabPosY, testedPrefabPosZ, testedPrefab,alreadyPlacedPrefabs);

            if (successPlacing) {
                openJoinPoints.remove(pointToConnectNewPrefabTo);
                for (var joinPoint : testedPrefab.getJoinPoints()) {
                    if (testedPrefabRandomJoinPoint.equals(joinPoint)) {
                        continue; // we succeeded in placing the room, but dont add the join point it was placed on
                    }
                    openJoinPoints.add(new Vector3i(testedPrefabPosX + joinPoint.x(), testedPrefabPosY + joinPoint.y(), testedPrefabPosZ + joinPoint.z()));

                }
                alreadyPlacedPrefabs.add(new PlacedRoomPrefab(testedPrefab,testedPrefabPosX,testedPrefabPosY,testedPrefabPosZ));
                return true;
            }
            // try to place room
        }
        return false;
    }
}
