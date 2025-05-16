package LevelLoadSystem.entitySpawnData;

import LevelLoadSystem.entitySpawnData.entityPrototypeData.EntitySpawnDataPrototype;
import com.jme3.math.Vector3f;
import game.entities.Entity;
import server.ServerLevelManager;

public class PrototypeEntitySpawnData extends EntitySpawnData{
    private final EntitySpawnDataPrototype<?> prototype;
    public PrototypeEntitySpawnData(EntitySpawnDataPrototype<?> prototype, Vector3f position) {
        super(-1, position);
        this.prototype = prototype;
    }

    @Override
    public Entity serverSpawn(ServerLevelManager serverLevelManager) {
        var resolved = prototype.resolve();
        if(resolved == null ){
            return null; // resolved to no entity - for example no item in randomization
        }
        resolved.getPosition().set(position);
        return resolved.serverSpawn(serverLevelManager);
    }
}
