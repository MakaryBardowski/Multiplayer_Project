package com.mccurry.game.menu.lobby.multiPlayer.host.launcher;

import com.mccurry.game.menu.lobby.model.SelectablePlayerClass;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HostGameLaunchParams {
    private final SelectablePlayerClass playerClass;
}
