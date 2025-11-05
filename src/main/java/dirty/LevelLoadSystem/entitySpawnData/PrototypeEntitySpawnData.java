package dirty.LevelLoadSystem.entitySpawnData;

import dirty.LevelLoadSystem.entitySpawnData.entityPrototypeData.EntitySpawnDataPrototype;
import com.jme3.math.Vector3f;
import dirty.game.entities.Entity;
import dirty.server.ServerContext;

public class PrototypeEntitySpawnData extends EntitySpawnData{
    private final EntitySpawnDataPrototype<?> prototype;
    public PrototypeEntitySpawnData(EntitySpawnDataPrototype<?> prototype, Vector3f position) {
        super(-1, position);
        this.prototype = prototype;
    }

    @Override
    public Entity serverSpawn(ServerContext ctx) {
        var resolved = prototype.resolve();
        if(resolved == null ){
            return null; // resolved to no entity - for example no item in randomization
        }
        resolved.getPosition().set(position);
        return resolved.serverSpawn(ctx);
    }
}
