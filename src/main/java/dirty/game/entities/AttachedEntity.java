package dirty.game.entities;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import lombok.Getter;
import dirty.server.ServerContext;

@Getter
public abstract class AttachedEntity extends Entity {
    protected Node node;

    public abstract void onInteract();


    public AttachedEntity(int id, String name, Node node) {
        super(id, name);
        this.node = node;
    }

    public abstract void setPositionClient(Vector3f newPos);
    public abstract void setPositionServer(ServerContext ctx, Vector3f newPos);
}
