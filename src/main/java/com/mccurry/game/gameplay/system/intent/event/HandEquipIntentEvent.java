package com.mccurry.game.gameplay.system.intent.event;

import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.system.equipment.model.HandSlot;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class HandEquipIntentEvent extends IntentEvent{
    private final Entity notValidatedCandidate;
    private final HandSlot slot;
    public HandEquipIntentEvent(@NotNull Entity intendingEntity, Entity notValidatedCandidate,  HandSlot handSlot) {
        super(intendingEntity);
        this.notValidatedCandidate = notValidatedCandidate;
        this.slot = handSlot;
    }
}
