package com.mccurry.game.gameplay.network.client;

import com.jme3.network.Client;
import com.jme3.network.Server;
import com.mccurry.game.gameplay.network.NetworkService;
import org.jetbrains.annotations.NotNull;

public class ClientNetworkService implements NetworkService {
    private @NotNull Client client;

    public ClientNetworkService(@NotNull Client client){
        this.client = client;
    }

    @Override
    public void destroy() {
        client.close();
    }
}
