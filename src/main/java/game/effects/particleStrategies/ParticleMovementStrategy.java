package game.effects.particleStrategies;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.effects.PhysicalParticleControl;
import game.map.collision.CollisionShape;
import game.map.collision.RectangleAABB;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class ParticleMovementStrategy {

    @Getter
    protected final Node node;
    protected final CollisionShape collisionShape;

    public abstract void updateParticle(float tpf);
    
    public abstract void move(float tpf);

    public abstract void onHitGround();

    protected abstract boolean hasNotHitGround();

    protected void removeControl() {
        node.removeControl(PhysicalParticleControl.class);
    }

}
