package com.mccurry.game.menu.lobby.singlePlayer.launcher;

import com.mccurry.game.menu.lobby.model.SelectablePlayerClass;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SinglePlayerGameLaunchParams {
    private final SelectablePlayerClass playerClass;
}
