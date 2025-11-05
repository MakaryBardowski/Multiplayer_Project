package dirty.LevelLoadSystem.entitySpawnData;

import com.jme3.math.Vector3f;
import dirty.game.entities.Entity;
import dirty.game.entities.factories.MobSpawnType;
import dirty.server.ServerContext;

public class MobSpawnData extends EntitySpawnData{
    public MobSpawnData(int templateIndex, Vector3f position) {
        super(templateIndex, position);
    }

    public MobSpawnData(MobSpawnType template, Vector3f position) {
        super(template.ordinal(), position);
    }

    @Override
    public Entity serverSpawn(ServerContext ctx) {
        var mob = ctx.getLevelManager().createAndRegisterMob(MobSpawnType.values()[templateIndex]);
        mob.setPositionServer(ctx,getPosition());
        return mob;
    }
}
