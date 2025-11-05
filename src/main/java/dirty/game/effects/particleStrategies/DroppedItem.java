package dirty.game.effects.particleStrategies;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dirty.game.items.Item;
import dirty.game.map.collision.RectangleAABB;

public class DroppedItem extends ParticleMovementStrategy {

    private final Vector3f velocity;
    private final Vector3f rotationVelocity;
    private final Item item;
    private static final float GRAVITY = 13.81f;//9.81f;
    private static final float BOUNCE_VELOCITY_MULTIPLIER = -0.7f;

    private final Vector3f tempVelocity = new Vector3f();
    public DroppedItem(Node node, Vector3f velocity, Vector3f rotationVelocity, Item item) {
        super(node,new RectangleAABB(node.getWorldTranslation(),item.getTemplate().getDropData().getOffset().x,item.getTemplate().getDropData().getOffset().y,item.getTemplate().getDropData().getOffset().z));
        this.velocity = velocity;
        this.rotationVelocity = rotationVelocity;
        this.item = item;
    }

    @Override
    public void updateParticle(float tpf) {
            tempVelocity.set(velocity.mult(tpf));
            node.getChild(1).rotate(rotationVelocity.x * tpf, rotationVelocity.y * tpf, rotationVelocity.z * tpf);
            velocity.subtractLocal(0, GRAVITY * tpf, 0);
            velocity.setX(velocity.x * 0.98f);
            velocity.setZ(velocity.z * 0.98f);

            if(hasNotCollidedVertically()) {
                moveY(tpf);
                moveXZ(tpf);
            }else{
                if(tempVelocity.y > 0){
                    velocity.setY(velocity.y* BOUNCE_VELOCITY_MULTIPLIER);
                    moveY(tpf);
                    moveXZ(tpf);
                } else {
                    onStop();
                }
            }
    }

    private void moveXZ(float tpf){
        if(doesNotCollideAfterMove(tempVelocity.x,0,0)) {
            moveX(tpf);
        }else{
            velocity.setX(velocity.x* BOUNCE_VELOCITY_MULTIPLIER);
        }
        if(doesNotCollideAfterMove(0,0,tempVelocity.z)) {
            moveZ(tpf);
        }else {
            velocity.setZ(velocity.z* BOUNCE_VELOCITY_MULTIPLIER);
        }
    }

    @Override
    public void onStop() {
        float[] startQ = new float[3];
        node.getChild(1).getWorldRotation().toAngles(startQ);
        startQ[0] = item.getTemplate().getDropData().getRotation().getX();
        startQ[2] = item.getTemplate().getDropData().getRotation().getZ();
        node.getChild(1).setLocalRotation(new Quaternion().fromAngles(startQ));
//        node.getWorldTranslation().addLocal(item.getTemplate().getDropData().getOffset());
        removeControl();
    }

    @Override
    protected boolean hasNotCollidedVertically() {
        return doesNotCollideAfterMove(0, tempVelocity.y, 0);
    }

    @Override
    public void moveX(float tpf) {
        node.move(tempVelocity.x,0,0);
    }

    @Override
    public void moveY(float tpf) {
        node.move(0,tempVelocity.y,0);
    }

    @Override
    public void moveZ(float tpf) {
        node.move(0,0,tempVelocity.z);
    }

}
