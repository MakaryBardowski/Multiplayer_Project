package com.mccurry.game.menu.lobby.multiPlayer.host.launcher;

import com.jme3.network.Server;
import com.mccurry.game.menu.lobby.model.SelectablePlayerClass;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
public class HostGameLaunchParams {
    @NotNull private final Server runningServer;
}
