package dirty.game.effects.particleStrategies;

import dirty.client.appStates.ClientGameAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dirty.game.map.collision.RectangleAABB;

import java.util.Random;

import static dirty.game.effects.DecalProjector.projectFromTo;

public class GoreParticle extends ParticleMovementStrategy {

    private final Vector3f velocity;
    private final Vector3f rotationVelocity;
    private static final float GRAVITY = 13.81f;//9.81f;
    private static final float HORIZONTAL_BOUNCE_VELOCITY_MULTIPLIER = -0.7f;

    private final Vector3f tempVelocity = new Vector3f();
    public GoreParticle(Node node, Vector3f velocity, Vector3f rotationVelocity) {
        super(node,new RectangleAABB(node.getWorldTranslation(),0.25f,0.25f,0.25f));
        this.velocity = velocity;
        this.rotationVelocity = rotationVelocity;
    }

    @Override
    public void updateParticle(float tpf) {
        tempVelocity.set(velocity.mult(tpf));
        node.rotate(rotationVelocity.x * tpf, rotationVelocity.y * tpf, rotationVelocity.z * tpf);

        if(hasNotCollidedVertically()) {
            moveY(tpf);
            if(doesNotCollideAfterMove(tempVelocity.x,0,0)) {
                moveX(tpf);
            }else{
                velocity.setX(velocity.x*HORIZONTAL_BOUNCE_VELOCITY_MULTIPLIER);
            }
            if(doesNotCollideAfterMove(0,0,tempVelocity.z)) {
                moveZ(tpf);
            }else {
                velocity.setZ(velocity.z*HORIZONTAL_BOUNCE_VELOCITY_MULTIPLIER);
            }
            velocity.subtractLocal(0, GRAVITY * tpf, 0);
        }else{
            onStop();
        }
    }

    @Override
    public void onStop() {
        float[] startQ = new float[3];
        node.getWorldRotation().toAngles(startQ);
        startQ[0] = 0;
        startQ[2] = 0;
        node.setLocalRotation(new Quaternion().fromAngles(startQ));
        projectFromTo(ClientGameAppState.getInstance(), node.getWorldTranslation().clone().add(0, 1, 0), new Vector3f(0, -1, 0),"Textures/Gameplay/Decals/testBlood" + new Random().nextInt(2) + ".png",new Random().nextInt(2)+2f);
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
