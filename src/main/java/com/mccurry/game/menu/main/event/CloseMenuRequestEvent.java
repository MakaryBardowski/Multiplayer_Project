package com.mccurry.game.menu.main.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.shared.ui.MenuType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CloseMenuRequestEvent implements Event {
    private final MenuType menuType;
}
