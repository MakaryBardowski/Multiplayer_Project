package com.mccurry.game.gameplay.system.damage.event;

import com.mccurry.events.core.Event;

import com.mccurry.game.gameplay.system.damage.model.DamageInstance;
import com.mccurry.game.gameplay.system.damage.model.DamageResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class DamageRequestEvent implements Event {
    @NotNull private final Integer entityId;
    @NotNull private final DamageInstance damageInstance;
    @Nullable private final Consumer<DamageResult> damageCallback;
}
