package com.mccurry.game.gameplay.menu.pausemenu.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.gameplay.menu.pausemenu.model.GameplayPauseState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class GameplayChangePauseStateRequest implements Event {
    private final @NotNull GameplayPauseState desiredNewState;
}
