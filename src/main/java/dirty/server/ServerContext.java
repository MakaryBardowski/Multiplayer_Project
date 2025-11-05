package dirty.server;

import com.jme3.network.Filter;
import com.jme3.network.HostedConnection;
import com.jme3.network.Server;
import com.jme3.scene.Node;
import dirty.core.GameContext;
import dirty.data.jumpToLevelData.BaseJumpToLevelData;
import dirty.game.entities.Entity;

import dirty.game.map.MapType;
import dirty.game.map.collision.WorldGrid;

import lombok.Setter;
import dirty.messages.TwoWayMessage;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.mccurry.map.model.Map;

public class ServerContext extends GameContext {
    private final Server networkServer;
    @Setter
    private ServerLevelManager levelManager;

    public ServerContext(Server networkServer, ServerLevelManager gameManager) {
        this.networkServer = networkServer;
        this.levelManager = gameManager;
    }

    @Deprecated
    public ServerLevelManager getLevelManager(){
        return levelManager;
    }

    // entity service
    public ConcurrentHashMap<Integer, Entity> getEntitiesById(){
        return levelManager.getEntitiesById();
    }

    public Entity addEntity(Entity entity){
        return levelManager.addEntity(entity);
    }

    public Entity getEntity(int id){
        return levelManager.getEntityByid(id);
    }

    public Entity removeEntity(int id){
        return levelManager.removeEntityById(id);
    }

    public boolean hasEntity(int id){
        return getEntity(id) != null;
    }

    public java.util.Map<Integer, HostedConnection> getConnectionsByPlayerEntityIds(){
        return levelManager.getHostsByPlayerId();
    }

    public HostedConnection getConnectionByPlayerEntityId(int id){
        return levelManager.getHostsByPlayerId().get(id);
    }

    // networking service

    public void notifySelectClients(TwoWayMessage message, Filter<HostedConnection> filter){
        networkServer.broadcast(filter, message);
    }

    public void notifyAllClients(TwoWayMessage message){
        networkServer.broadcast(message);
    }

    public Collection<HostedConnection> getConnectedClients(){
        return networkServer.getConnections();
    }

    public HostedConnection getConnectedClient(int i){
        return networkServer.getConnection(i);
    }

    public void broadcastEntityOnNextLevel(Entity entity){
        levelManager.broadcastEntityOnNextLevel(entity);
    }

    // map service
    public Node getRootNode(){
        return levelManager.getRootNode();
    }

    public int getCurrentLevelIndex(){
        return levelManager.getCurrentLevelIndex();
    }

    public long[] getLevelSeeds(){
        return levelManager.getLevelSeeds();
    }

    public MapType[] getLevelTypes(){
        return levelManager.getLevelTypes();
    }

    public void jumpToLevel(BaseJumpToLevelData jumpToLevelData){
        levelManager.jumpToLevel(jumpToLevelData);
    }

    public Map getCurrentMap(){
        return levelManager.getCurrentMap();
    }

    public WorldGrid getCollisionGrid(){
        return levelManager.getCollisionGrid();
    }
}
