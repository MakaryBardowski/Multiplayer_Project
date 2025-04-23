package game.map.MapDataModel;

import com.jme3.math.Vector3f;
import lombok.Getter;

@Getter
public class Map extends TileMap{
    private final int blockSize;

    public Map(byte[] logicMap,int mapSizeX, int mapSizeY, int mapSizeZ, int blockSize){
        super(logicMap,mapSizeX,mapSizeY,mapSizeZ);
        this.blockSize = blockSize;
    }


    public boolean isWithinMapBounds(Vector3f position){
        return positionToIndex(position) < logicMap.length;
    }

    public boolean isPositionEmpty(Vector3f position){
        return getBlockIdAtPosition(position) == 0;
    }

    public boolean isPositionNotEmpty(Vector3f position){
        return !isPositionEmpty(position);
    }

    public void setBlockIdAtPosition(Vector3f position, byte blockId) {
        logicMap[positionToIndex(position)] = blockId;
    }

    public byte getBlockIdAtPosition(Vector3f position) {
        return logicMap[positionToIndex(position)];
    }

    private int positionToIndex(Vector3f position){
        return positionToIndex(
                (int) (Math.floor(position.x / blockSize)),
                (int) (Math.floor(position.y / blockSize)),
                (int) (Math.floor(position.z / blockSize))
        );
    }

}
