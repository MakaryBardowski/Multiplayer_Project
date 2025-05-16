package server;

import com.jme3.network.Filter;
import com.jme3.network.HostedConnection;
import com.jme3.network.Server;
import data.jumpToLevelData.BaseJumpToLevelData;
import game.entities.Entity;
import lombok.Setter;
import messages.TwoWayMessage;

import java.util.concurrent.ConcurrentHashMap;

public class ServerContext {
    private final Server networkServer;
    @Setter
    private LevelManager<BaseJumpToLevelData> levelManager;

    public ServerContext(Server networkServer, LevelManager<BaseJumpToLevelData> gameManager) {
        this.networkServer = networkServer;
        this.levelManager = gameManager;
    }

    public ConcurrentHashMap<Integer, Entity> getEntitiesById(){
        return levelManager.getEntitiesById();
    }

    public Entity getEntity(int id){
        return levelManager.getEntityByid(id);
    }

    public void notifySelectClients(TwoWayMessage message, Filter<HostedConnection> filter){
        networkServer.broadcast(filter, message);
    }

    public void notifyAllClients(TwoWayMessage message){
        networkServer.broadcast(message);
    }
}
