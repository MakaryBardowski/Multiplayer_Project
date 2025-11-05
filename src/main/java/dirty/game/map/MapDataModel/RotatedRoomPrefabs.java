package dirty.game.map.MapDataModel;

import dirty.game.map.AxisAlignedRotation;

public class RotatedRoomPrefabs {
    private final RoomPrefab facingNorth;
    private final RoomPrefab facingEast;
    private final RoomPrefab facingSouth;
    private final RoomPrefab facingWest;

    /**
     * @param roomPrefab north-facing (AxisAlignedRotation.FACE_NORTH_Z_AXIS) prefab that will be rotated and stored.
     */
    public RotatedRoomPrefabs(RoomPrefab roomPrefab){
        var facingWest = rotateCounterClockwiseAxisY(roomPrefab);
        var facingSouth = rotateCounterClockwiseAxisY(facingWest);
        var facingEast = rotateCounterClockwiseAxisY(facingSouth);
        this.facingNorth = roomPrefab;
        this.facingEast = facingEast;
        this.facingSouth = facingSouth;
        this.facingWest = facingWest;
    }

    public static RoomPrefab rotateCounterClockwiseAxisY(RoomPrefab room) {
        final int M = room.getMapSizeX();
        final int N = room.getMapSizeZ();
        final int roomHeight = room.getMapSizeY();

        byte[] rotatedMap = new byte[room.getMaxBlockCount()];
        // size X/Z are swapped because of rotation
        var rotatedPrefab = new RoomPrefab(rotatedMap,room.getMapSizeZ(),room.getMapSizeY(),room.getMapSizeX());

        for (int x = 0; x < M; x++) {
            for (int z = 0; z < N; z++) {
                // rotate as usual, just when we want to rotate an element, rotate the whole column (whole height of the room)
                for(int y = 0; y < roomHeight; y++) {
                    var blockToRotate = room.getBlockIdAtPosition(x,y,z);
                    rotatedPrefab.setBlockIdAtPosition(z,y,M - 1 - x, blockToRotate);
                }
            }
        }

        for(var joinPoint : room.getJoinPoints()){
            rotatedPrefab.addJoinPoint(joinPoint.z(),joinPoint.y(),M-1-joinPoint.x());
        }

        rotatedPrefab.setEntitySpawnDataPrototypesByResultId(room.getEntitySpawnDataPrototypesByResultId());

        for(var entitySpawnData : room.getEntitySpawnData()){
            var esdPosition = entitySpawnData.getPosition();
            esdPosition.set(esdPosition.z,esdPosition.y,M-esdPosition.x);
            rotatedPrefab.addEntitySpawnData(entitySpawnData);
        }

        for(var exitSpawnpoint : room.getPotentialExitSpawnpoints()){
            rotatedPrefab.addPotentialExitSpawnpoint(exitSpawnpoint.z,exitSpawnpoint.y,M-exitSpawnpoint.x);
        }


        return rotatedPrefab;
    }

    public RoomPrefab get(AxisAlignedRotation rotation){
        if(rotation == AxisAlignedRotation.FACE_NORTH_PLUS_Z_AXIS){
            return facingNorth;
        } else if (rotation == AxisAlignedRotation.FACE_EAST_MINUS_X_AXIS) {
            return facingEast;
        } else if (rotation == AxisAlignedRotation.FACE_SOUTH_MINUS_Z_AXIS) {
            return facingSouth;
        }
        return facingWest;
    }
}
