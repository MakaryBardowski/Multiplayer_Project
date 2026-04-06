package com.mccurry.game.gameplay.system.intent.event;

import com.mccurry.events.core.Event;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public abstract class IntentEvent implements Event {
    @NotNull private final Entity intendingEntity;
}
