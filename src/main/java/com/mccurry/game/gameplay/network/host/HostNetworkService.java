package com.mccurry.game.gameplay.network.host;

import com.jme3.network.Server;
import com.mccurry.game.gameplay.network.NetworkService;
import org.jetbrains.annotations.NotNull;

public class HostNetworkService implements NetworkService {
    private @NotNull Server server;

    public HostNetworkService(@NotNull Server server){
        this.server = server;
    }

    @Override
    public void destroy() {
        server.close();
    }
}
