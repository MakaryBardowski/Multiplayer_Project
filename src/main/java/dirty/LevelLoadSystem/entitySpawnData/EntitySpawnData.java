package dirty.LevelLoadSystem.entitySpawnData;

import com.jme3.math.Vector3f;
import dirty.game.entities.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import dirty.server.ServerContext;

@Getter
@ToString
@AllArgsConstructor
public abstract class EntitySpawnData implements Cloneable {
    protected int templateIndex;
    protected Vector3f position;

    public abstract Entity serverSpawn(ServerContext ctx);

    @Override
    public EntitySpawnData clone() {
        try {
            EntitySpawnData clone = (EntitySpawnData) super.clone();
            clone.position = new Vector3f(position.x,position.y,position.z);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
