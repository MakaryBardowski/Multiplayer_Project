package server;

import data.jumpToLevelData.BaseJumpToLevelData;
import game.entities.Entity;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

public abstract class LevelManager<T extends BaseJumpToLevelData>{
    @Getter
    protected int currentLevelIndex;

    @Getter
    protected final ConcurrentHashMap<Integer, Entity> entitiesById = new ConcurrentHashMap<>();

    public abstract void jumpToLevel(T jumpToLevelData);
    public abstract void cleanup();

    public Entity getEntityByid(int id){
        return entitiesById.get(id);
    }

}
