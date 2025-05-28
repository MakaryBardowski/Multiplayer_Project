package LevelLoadSystem.entitySpawnData;

import com.jme3.math.Vector3f;
import game.entities.DecorationTemplates;
import game.entities.Entity;
import server.ServerContext;

public class IndestructibleDecorationSpawnData extends EntitySpawnData{
    public IndestructibleDecorationSpawnData(int templateIndex, Vector3f position) {
        super(templateIndex, position);
    }

    @Override
    public Entity serverSpawn(ServerContext ctx) {
        return ctx.getLevelManager().createAndRegisterIndestructibleDecoration(DecorationTemplates.getTemplateByIndex(templateIndex),this.getPosition());
    }
}
