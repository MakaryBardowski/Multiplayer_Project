package dirty.messages;

import dirty.client.appStates.ClientGameAppState;
import dirty.client.Main;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import dirty.game.entities.Chest;
import dirty.game.entities.Destructible;
import dirty.game.entities.Entity;
import dirty.game.entities.mobs.Mob;
import dirty.game.entities.mobs.player.Player;
import dirty.game.items.Item;
import dirty.server.ServerContext;

@Serializable
public abstract class TwoWayMessage extends AbstractMessage {

    public abstract void handleServer(ServerContext ctx, HostedConnection sender);

    public abstract void handleClient(ClientGameAppState client);

    protected Chest getChestByIdClient(int id) {
        return ((Chest) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected Chest getChestByIdServer(ServerContext ctx, int id) {
        return ((Chest) ctx.getEntity(id));
    }

    protected Entity getEntityByIdServer(ServerContext ctx, int id) {
        return ctx.getEntity(id);
    }

    protected Mob getMobByIdServer(ServerContext ctx, int id) {
        return ((Mob) ctx.getEntity(id));
    }

    protected Item getItemByIdServer(ServerContext ctx, int id) {
        return ((Item) ctx.getEntity(id));
    }

    protected Destructible getDestructibleByIdServer(ServerContext ctx, int id) {
        return ((Destructible) ctx.getEntity(id));
    }

    protected Entity getEntityByIdClient(int id) {
        return ClientGameAppState.getInstance().getMobs().get(id);
    }

    protected Mob getMobByIdClient(int id) {
        return ((Mob) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected Item getItemByIdClient(int id) {
        return ((Item) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected Destructible getDestructibleByIdClient(int id) {
        return ((Destructible) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected void removeItemFromMobEquipmentClient(int mobId, int itemId) {
        var mob = getMobByIdClient(mobId);
        var item = getItemByIdClient(itemId);
        var mobEquipment = mob.getEquipment();
        mobEquipment.removeItem(item);
    }

    protected boolean entityExistsLocallyClient(int mobId) {
        return ClientGameAppState.getInstance().getMobs().get(mobId) != null;
    }

    protected boolean entityExistsLocallyServer(ServerContext ctx, int mobId) {
        return  null != ctx.getEntity(mobId);
    }

    protected boolean entityNotExistsLocallyClient(int mobId) {
        return ClientGameAppState.getInstance().getMobs().get(mobId) == null;
    }

    protected boolean entityNotExistsLocallyServer(ServerContext ctx, int mobId) {
        return ctx.getEntity(mobId) == null;
    }

    protected HostedConnection getHostedConnectionByPlayer(ServerContext ctx, Player p) {
        return ctx.getConnectionByPlayerEntityId(p.getId());
    }

    protected void placeMob(Vector3f pos, Mob p) {
        ClientGameAppState.getInstance().getDestructibleNode().attachChild(p.getNode());
        p.getNode().setLocalTranslation(pos);
        ClientGameAppState.getInstance().getGrid().insert(p);
    }

    protected void enqueueExecution(Runnable runnable) {
        Main.getInstance().enqueue(runnable);
    }
}
