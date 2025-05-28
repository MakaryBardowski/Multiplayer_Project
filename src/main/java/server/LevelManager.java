package server;

import com.jme3.scene.Node;
import data.jumpToLevelData.BaseJumpToLevelData;
import game.entities.Entity;
import com.mccurry.map.model.Map;
import game.map.MapType;
import game.map.collision.WorldGrid;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

public abstract class LevelManager<T extends BaseJumpToLevelData>{
    @Getter
    protected final Node rootNode;

    protected int nextEntityId;

    @Getter
    protected int currentLevelIndex;

    @Getter
    @Setter
    protected long[] levelSeeds;

    @Getter
    @Setter
    protected MapType[] levelTypes;

    @Getter
    protected Map currentMap;

    @Getter
    protected WorldGrid collisionGrid;

    @Getter
    protected final ConcurrentHashMap<Integer, Entity> entitiesById = new ConcurrentHashMap<>();

    protected LevelManager(Node rootNode) {
        this.rootNode = rootNode;
    }

    public abstract void jumpToLevel(T jumpToLevelData);
    public abstract void cleanup();

    public Entity addEntity(Entity entity){
        return entitiesById.put(nextEntityId++, entity);
    }

    public Entity getEntityByid(int id){
        return entitiesById.get(id);
    }

    public Entity removeEntityById(int id){
        return entitiesById.remove(id);
    }
}
