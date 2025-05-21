package game.map;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;

import java.util.List;
import java.util.Random;

import server.ServerContext;
import server.ServerGameAppState;

public class EntitySpawner {

    private final Random random;
    private final int blockSize;

    public EntitySpawner(long seed, int blockSize) {
        random = new Random(seed);
        this.blockSize = blockSize;
    }


    public void spawnNewLevelEntities(ServerContext ctx, List<EntitySpawnData> entitySpawnData){
        for(var entitySpawnInfo : entitySpawnData){
            var serverLevelManager = ctx.getLevelManager();
            entitySpawnInfo.getPosition().multLocal(blockSize);

            var spawnedEntity =entitySpawnInfo.serverSpawn(ctx,serverLevelManager);
            if(spawnedEntity != null){
                serverLevelManager.broadcastEntityOnNextLevel(spawnedEntity);
            }
        }
    }

}
