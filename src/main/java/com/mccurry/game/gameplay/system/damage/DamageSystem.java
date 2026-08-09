package com.mccurry.game.gameplay.system.damage;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.damage.event.DamageRequestEvent;
import com.mccurry.game.gameplay.system.damage.model.DamageInstance;
import com.mccurry.game.gameplay.system.damage.model.DamageResult;
import com.mccurry.game.gameplay.system.stats.StatisticsComponent;
import com.mccurry.game.gameplay.system.stats.health.Health;
import com.mccurry.game.gameplay.system.stats.health.HealthCalculationSystem;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class DamageSystem implements EventSubscriber {
    private final EntityStore entityStore;
    private final HealthCalculationSystem healthCalculationSystem;


    private final SyncEventHandler<DamageRequestEvent> onDamageRequest = (event, lifecycleManager) -> {
        dealDamage(event.getEntityId(), event.getDamageInstance(), event.getDamageCallback());
    };

    public void dealDamage(@NotNull Integer entityId, @NotNull DamageInstance damageInstance, @Nullable Consumer<DamageResult> damageResultCallback) {
        var targetStats = entityStore.getComponent(entityId, StatisticsComponent.class);

        if (targetStats == null) {
            return;
        }


        var targetHealth = targetStats.getHealth();
        if (targetHealth != null) {
            applyInstance(entityId,targetHealth, damageInstance, damageResultCallback);
        }
    }

    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(DamageRequestEvent.class, onDamageRequest);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(DamageRequestEvent.class, onDamageRequest);
    }

    private void applyInstance(@NotNull Integer targetEntityId, @NotNull Health targetHealth, @NotNull DamageInstance damageInstance, @Nullable Consumer<DamageResult> damageResultCallback) {
        var damageResult = new DamageResult(targetEntityId);
        if (damageInstance.isCancelled()) {
            damageResult.setTargetDied(false);
            damageResult.setDamageCancelled(true);
            runDamageCallback(damageResultCallback, damageResult);
            return;
        }

        var physicalDamage = damageInstance.getPhysical();
        if (physicalDamage.isCancelled()) {
            damageResult.setTargetDied(false);
            damageResult.setDamageCancelled(true);
            runDamageCallback(damageResultCallback, damageResult);
            return;
        }

        healthCalculationSystem.damage(targetHealth, physicalDamage.getDamageValue());
        damageResult.setTargetDied(targetHealth.getCurrent() <= 0);
        damageResult.setDamageCancelled(false);
        runDamageCallback(damageResultCallback, damageResult);


        if(targetHealth.getCurrent() <= 0){
            var transfer = entityStore.getComponent(targetEntityId, TransformComponent.class);
            if(transfer != null){
                entityStore.destroy(targetEntityId);
                transfer.getOriginNode().removeFromParent();
            }
        }
    }

    private void runDamageCallback(@Nullable Consumer<DamageResult> damageResultCallback, @NotNull DamageResult damageResult) {
        if (damageResultCallback == null) {
            return;
        }

        damageResultCallback.accept(damageResult);
    }
}
