package debugging;

import behaviorTree.context.SimpleHumanMobContext;
import client.appStates.ClientGameAppState;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import game.map.collision.CollisionDebugUtils;
import server.ServerGameAppState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollisionShapeDebugControl extends AbstractControl {

    private Mob mob;
    private Geometry collsionShapeDebug;
    public CollisionShapeDebugControl(Mob mob) {
        this.mob = mob;

        collsionShapeDebug = CollisionDebugUtils.createHitboxGeometry(mob.getCollisionShape().getWidth(), mob.getCollisionShape().getHeight(), mob.getCollisionShape().getLength(), ColorRGBA.Green);

        ClientGameAppState.getInstance().getDebugNode().attachChild(collsionShapeDebug);
    }

    @Override
    protected void controlUpdate(float tpf) {
            collsionShapeDebug.setLocalTranslation(mob.getCollisionShape().getPosition().clone());

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);

    }
}
