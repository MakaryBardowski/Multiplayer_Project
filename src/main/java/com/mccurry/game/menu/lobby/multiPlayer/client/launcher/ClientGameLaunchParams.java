package com.mccurry.game.menu.lobby.multiPlayer.client.launcher;

import com.jme3.network.Client;
import com.jme3.network.Server;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
public class ClientGameLaunchParams {
    @NotNull private final Client connectedClient;
}
