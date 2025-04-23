package game.map.MapDataModel;

import com.jme3.math.Vector3f;
import lombok.Getter;

@Getter
public class PlacedRoomPrefab extends RoomPrefab{
    private final byte posX;
    private final byte posY;
    private final byte posZ;
    public PlacedRoomPrefab(RoomPrefab prefab, int x, int y, int z) {
        super(prefab.logicMap,prefab.mapSizeX,prefab.mapSizeY,prefab.mapSizeZ);
        this.joinPoints.addAll(prefab.getJoinPoints());
        this.entitySpawnData.addAll(prefab.getEntitySpawnData());
        this.posX = (byte) x;
        this.posY = (byte) y;
        this.posZ = (byte) z;
    }

    public boolean overlapsPrefab(TileMap otherPrefab, int otherPosX, int otherPosY, int otherPosZ, int shrinkBorder){
        if (otherPrefab == this) {
            return false;
        }

        // shrink border means how many blocks to ignore for collision sake

        float tMinX = posX + shrinkBorder;
        float tMaxX = posX + mapSizeX - shrinkBorder;
        float tMinY = posY + shrinkBorder;
        float tMaxY = posY + mapSizeY - shrinkBorder;
        float tMinZ = posZ + shrinkBorder;
        float tMaxZ = posZ + mapSizeZ - shrinkBorder;

        float oMinX = otherPosX + shrinkBorder;
        float oMaxX = otherPosX + otherPrefab.mapSizeX - shrinkBorder;
        float oMinY = otherPosY + shrinkBorder;
        float oMaxY = otherPosY + otherPrefab.mapSizeY - shrinkBorder;
        float oMinZ = otherPosZ + shrinkBorder;
        float oMaxZ = otherPosZ + otherPrefab.mapSizeZ - shrinkBorder;

        if (tMaxX < oMinX || tMinX > oMaxX
                || tMaxY < oMinY || tMinY > oMaxY
                || tMaxZ < oMinZ || tMinZ > oMaxZ) {
            return false;
        }

        return true;
    }
}
