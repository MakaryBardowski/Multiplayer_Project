package com.mccurry.game.menu.lobby.multiPlayer.host.model;

import com.jme3.network.HostedConnection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ClientRegistry {
    private static final int MINIMUM_CLIENT_INDEX = 0;
    private final int maxClientCount;
    private final Map<Integer, HostedConnection> clients;

    public ClientRegistry(int maxClientCount){
        this.maxClientCount = maxClientCount;
        this.clients = new HashMap<>(this.maxClientCount);
    }

    public @Nullable Integer registerClient(HostedConnection connection){
        var playerIndex = findAvailableClientIndex();
        if(playerIndex == null){
            return null;
        }
        clients.put(playerIndex,connection);
        return playerIndex;
    }

    public @Nullable HostedConnection deregisterClient(HostedConnection connection){
        var iterator = clients.entrySet().iterator();

        while(iterator.hasNext()){
            var entry = iterator.next();
            var client = entry.getValue();
            if(client.equals(connection)){
                iterator.remove();
                return client;
            }
        }

        return null;
    }

    public @Nullable Integer getClientIndex(HostedConnection connection){
        for (Map.Entry<Integer, HostedConnection> entry : clients.entrySet()) {
            var client = entry.getValue();
            if (client.equals(connection)) {
                return entry.getKey();
            }
        }

        return null;
    }

    private @Nullable Integer findAvailableClientIndex(){
        for(int i = MINIMUM_CLIENT_INDEX; i < maxClientCount; i++){
            if(!clients.containsKey(i)){
                return i;
            }
        }

        return null;
    }
}
