package dirty.game.effects.particleStrategies;

import dirty.client.appStates.ClientGameAppState;
import com.jme3.scene.Node;
import dirty.game.effects.PhysicalParticleControl;
import dirty.game.entities.Collidable;
import dirty.game.entities.mobs.Mob;
import dirty.game.map.collision.CollisionShape;
import lombok.Getter;

public abstract class ParticleMovementStrategy {
    private float[][] corners = new float[8][3];

    @Getter
    protected final Node node;
    protected final CollisionShape collisionShape;

    public abstract void updateParticle(float tpf);
    
    public abstract void moveX(float tpf);
    public abstract void moveY(float tpf);
    public abstract void moveZ(float tpf);

    public abstract void onStop();

    protected abstract boolean hasNotCollidedVertically();

    protected void removeControl() {
        node.removeControl(PhysicalParticleControl.class);
    }

    public ParticleMovementStrategy(Node node, CollisionShape collisionShape){
        this.node = node;
        this.collisionShape = collisionShape;
    }

    public boolean doesNotCollideAfterMove(float moveX, float moveY, float moveZ) {
        var newPos = collisionShape.getPosition().add(moveX,moveY,moveZ);
        float centerX = newPos.getX();
        float centerY = newPos.getY();
        float centerZ = newPos.getZ();
        float width = collisionShape.getWidth();
        float height = collisionShape.getHeight(); // height == 1.25??
        float depth = collisionShape.getLength();

        corners[0] = new float[]{centerX - width, centerY + height, centerZ - depth};
        corners[1] = new float[]{centerX + width, centerY + height, centerZ - depth};
        corners[2] = new float[]{centerX - width, centerY + height, centerZ + depth};
        corners[3] = new float[]{centerX + width, centerY + height, centerZ + depth};
        corners[4] = new float[]{centerX - width, centerY, centerZ - depth};
        corners[5] = new float[]{centerX + width, centerY, centerZ - depth};
        corners[6] = new float[]{centerX - width, centerY, centerZ + depth};
        corners[7] = new float[]{centerX + width, centerY, centerZ + depth};
        var cellSize = ClientGameAppState.getInstance().getBLOCK_SIZE();
        for (var corner : corners) {
            int x = (int) (Math.floor(corner[0] / cellSize));
            int y = (int) (Math.floor(corner[1] / cellSize));
            int z = (int) (Math.floor(corner[2] / cellSize));

            if (ClientGameAppState.getInstance().getMap().getBlockWorld().getLogicMap().isPositionNotEmpty(x,y,z)) {
                return false;
            }
        }
        // above is wall collision

        for (Collidable m : ClientGameAppState.getInstance().getGrid().getNearbyCollisionShapeAtPos(newPos, collisionShape)) {
            if (!(m instanceof Mob) && collisionShape.wouldCollideAtPosition(m.getCollisionShape(), newPos)) {
                return false;
            }
        }

        return true;
    }


}
