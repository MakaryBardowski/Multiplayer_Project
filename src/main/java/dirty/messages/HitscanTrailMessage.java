package dirty.messages;

import dirty.client.appStates.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import dirty.game.effects.Vfx;
import dirty.game.effects.VfxType;
import dirty.game.entities.mobs.Mob;
import dirty.game.items.weapons.Rifle;
import lombok.Getter;
import dirty.server.ServerContext;

@Serializable
public class HitscanTrailMessage extends EntityUpdateMessage {
    @Getter
    protected int clientId;
    protected float targetX;
    protected float targetY;
    protected float targetZ;

    protected float normalX;
    protected float normalY;
    protected float normalZ;

    private int vfxTypeOrdinal;

    public HitscanTrailMessage() {
    }

    public HitscanTrailMessage(int id, Vector3f shotPos, Vector3f normal, int hostedConnectionId, VfxType vfxType) {
        super(id);
        this.targetX = shotPos.getX();
        this.targetY = shotPos.getY();
        this.targetZ = shotPos.getZ();
        this.normalX = normal.getX();
        this.normalY = normal.getY();
        this.normalZ = normal.getZ();
        this.clientId = hostedConnectionId;
        this.vfxTypeOrdinal = vfxType.ordinal();
    }

    public Vector3f getShotPos(){
    return new Vector3f(targetX,targetY,targetZ);
    }
    public Vector3f getShotNormal(){
        return new Vector3f(normalX,normalY,normalZ);
    }
    public VfxType getVfxType() { return VfxType.values()[vfxTypeOrdinal];}

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
//            HostedConnection hc = ServerMain.getInstance().getServer().getConnection(clientId);
            ctx.notifySelectClients(this,Filters.notIn(hc));
    }

    @Override
    public void handleClient(ClientGameAppState client) {
    enqueueExecution(() -> {
            Mob mob = (Mob) getMobByIdClient(id);
            Rifle.createBullet(mob.getNode().getWorldTranslation().clone().add(0, 1, 0), getShotPos());
            var normal = getShotNormal();
            var desiredVfx = Vfx.getEffect(getVfxType());
            if(desiredVfx != null) {
                desiredVfx.playAt(client.getDebugNode(), getShotPos().add(normal.mult(0.3f)), normal);
            }
    });
    }
    

}
