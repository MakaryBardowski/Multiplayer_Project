package messages;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.math.Vector3f;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Collidable;
import game.entities.mobs.player.Player;
import game.map.collision.MovementCollisionUtils;
import game.map.collision.WorldGrid;
import java.util.ArrayList;

import server.ServerContext;
import server.ServerGameAppState;

@Serializable
public class PlayerPosUpdateRequest extends EntityUpdateMessage {
    protected int levelIndex;
    protected float x;
    protected float y;
    protected float z;

    public PlayerPosUpdateRequest() {
    }

    public PlayerPosUpdateRequest(int levelIndex, int id, Vector3f pos) {
        super(id);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.levelIndex = levelIndex;
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

    @Override
    public String toString() {
        return "MobUpdatePosRotMessage{ id=" + id + " x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
        if(ctx.getCurrentLevelIndex() != levelIndex){
            return;
        }
        if (entityExistsLocallyServer(ctx,id)) {
            Player p = (Player) ctx.getEntity(id);
            if(!p.isAbleToMove()){
                return;
            }

            var grid = ctx.getCollisionGrid();
            var allCollidables = grid.getNearbyAtPosition(p, getPos());
            var solid = new ArrayList<Collidable>();
            var passable = new ArrayList<Collidable>();
            MovementCollisionUtils.sortByPassability(allCollidables, solid, passable);

            if (MovementCollisionUtils.isValidMobMovement(p, getPos(), grid, solid)) {
                // this whole thing should be moved to moveServer override in player
                grid.remove(p);

                Main.getInstance().enqueue(() -> {
                    if (!ctx.hasEntity(id)) {
                        return;
                    }
                    var newPos = getPos();
                    p.getNode().setLocalTranslation(newPos);
                    grid.insert(p);
                    
                    MovementCollisionUtils.checkPassableCollisions(ctx, p, grid, passable);
                    p.getPositionChangedOnServer().set(true);

                });
            } else {
                InstantEntityPosCorrectionMessage corrMsg = new InstantEntityPosCorrectionMessage(p, p.getNode().getWorldTranslation());
                ctx.notifySelectClients(corrMsg, Filters.in(getHostedConnectionByPlayer(ctx,p)));
            }

        }
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        throw new UnsupportedOperationException("client got back a request.");
    }

}
