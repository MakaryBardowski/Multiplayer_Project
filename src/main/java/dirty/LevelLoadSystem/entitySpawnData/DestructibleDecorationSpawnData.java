package dirty.LevelLoadSystem.entitySpawnData;

import com.jme3.math.Vector3f;
import dirty.game.entities.DecorationTemplates;
import dirty.game.entities.Entity;
import dirty.server.ServerContext;

public class DestructibleDecorationSpawnData extends EntitySpawnData{
    public DestructibleDecorationSpawnData(int templateIndex, Vector3f position) {
        super(templateIndex, position);
    }

    @Override
    public Entity serverSpawn(ServerContext ctx) {
        return ctx.getLevelManager().createAndRegisterDestructibleDecoration(DecorationTemplates.getTemplateByIndex(templateIndex),this.getPosition());
    }
}
