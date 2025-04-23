package game.map.MapDataModel;

import com.jme3.math.Vector3f;
import game.map.blocks.BlockType;
import game.map.collision.RectangleAABB;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class TileMap {

    protected final int mapSizeX;

    protected final int mapSizeY;

    protected final int mapSizeZ;

    protected final byte[] logicMap;

    public TileMap(byte[] logicMap,int mapSizeX, int mapSizeY, int mapSizeZ){
        this.logicMap = logicMap;
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        this.mapSizeZ = mapSizeZ;
    }

    public byte getBlockIdAtIndex(int index){
        return logicMap[index];
    }
    public void setBlockIdAtIndex(int index,byte value){
        logicMap[index] = value;
    }

    public byte getBlockIdAtPosition(int x, int y,int z){
        return logicMap[positionToIndex(x,y,z)];
    }


    public void setBlockIdAtPosition(int x, int y,int z, byte blockId){
        logicMap[positionToIndex(x,y,z)] = blockId;
    }


    public boolean isPositionEmpty(int x, int y, int z){
        return getBlockIdAtPosition(x,y,z) == 0;
    }



    public boolean isPositionNotEmpty(int x, int y, int z){
        return !isPositionEmpty(x,y,z);
    }



    public boolean isWithinMapBounds(int x,int y, int z){
        int index = positionToIndex(x,y,z);
        return index < logicMap.length && index >= 0;
    }

    protected int positionToIndex(int x, int y, int z){
        return x * (mapSizeY * mapSizeZ) + y * mapSizeZ + z;
    }

    public int getXFromIndex(int index) {
        return  index / (mapSizeY * mapSizeZ);
    }

    public int getYFromIndex(int index) {
        int y = (index % (mapSizeY * mapSizeZ)) / mapSizeZ;
        return y;
    }

    public int getZFromIndex(int index) {
        int z = index % mapSizeZ;
        return z;
    }

    public int getMaxBlockCount(){
        return logicMap.length;
    }

    public static int positionToIndex(int x, int y, int z,int mapSizeY,int mapSizeZ){
        return x * (mapSizeY * mapSizeZ) + y * mapSizeZ + z;
    }

    public static int positionToIndex(Vector3f position,int mapSizeY,int mapSizeZ, int blockSize){
        return Map.positionToIndex(
                (int) (Math.floor(position.x / blockSize)),
                (int) (Math.floor(position.y / blockSize)),
                (int) (Math.floor(position.z / blockSize)),
                mapSizeY,
                mapSizeZ
        );
    }


    public boolean placePrefab(int prefabPosX, int prefabPosY, int prefabPosZ, TileMap prefab, List<PlacedRoomPrefab> alreadyPlacedPrefabs){
        boolean success = true;
        var prefabMap = prefab.getLogicMap();

        if(
                prefabPosX < 0 ||  prefabPosY < 0 || prefabPosZ < 0
                ||
                prefabPosX+prefab.mapSizeX >= this.mapSizeX || prefabPosY+prefab.mapSizeY >= this.mapSizeY || prefabPosZ+prefab.mapSizeZ >= this.mapSizeZ ){
            return false;
        }

        for(var alreadyPlacedPrefab : alreadyPlacedPrefabs){
            if(alreadyPlacedPrefab.overlapsPrefab(prefab,prefabPosX,prefabPosY,prefabPosZ,1)){
               return false;
            }
        }

        // place prefab
        for(int i = 0; i < prefabMap.length;i++) {
            var prefabBlock = prefab.getBlockIdAtIndex(i);
            var prefabBlockX = prefab.getXFromIndex(i);
            var prefabBlockY = prefab.getYFromIndex(i);
            var prefabBlockZ = prefab.getZFromIndex(i);
            setBlockIdAtPosition(prefabPosX + prefabBlockX, prefabPosY + prefabBlockY, prefabPosZ + prefabBlockZ, prefabBlock);
        }
        return success;
    }
}
