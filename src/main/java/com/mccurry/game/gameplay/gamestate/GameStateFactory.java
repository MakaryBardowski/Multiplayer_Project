package com.mccurry.game.gameplay.gamestate;

import com.jme3.network.Client;
import com.jme3.network.Server;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.network.client.ClientNetworkService;
import com.mccurry.game.gameplay.network.singleplayer.NoOpNetworkService;
import com.mccurry.game.gameplay.network.host.HostNetworkService;

public class GameStateFactory {
    public static GameState singleplayer(){
        var entityStore = new EntityStore();
        return new GameState(entityStore,new NoOpNetworkService());
    }

    public static GameState host(Server runningServer){
        var entityStore = new EntityStore();
        var serverNetworkService = new HostNetworkService(runningServer);
        return new GameState(entityStore, serverNetworkService);
    }

    public static GameState client(Client connectedClient){
        var entityStore = new EntityStore();
        var clientNetworkService = new ClientNetworkService(connectedClient);
        return new GameState(entityStore, clientNetworkService);
    }
}
