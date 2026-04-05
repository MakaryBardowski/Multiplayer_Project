package com.mccurry.game.gameplay.system.attack;

import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.component.WeaponComponent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttackSystem {
    private final EntityStore entityStore;

    public void processAttack(Entity attacker, Entity weaponEntity, WeaponComponent weaponComponent, double gameTimeSeconds){
        if(weaponComponent.getLastUsedGameTimeSeconds() + weaponComponent.getCooldownSeconds() > gameTimeSeconds){
            return;
        }
        weaponComponent.setLastUsedGameTimeSeconds(gameTimeSeconds);
        System.err.println("Entity %s attacks with weapon %s - dealing: %s damage. It will be available for use in %s seconds!".formatted(
                attacker.getId(),
                weaponEntity.getId(),
                        weaponComponent.getBaseDamage(),
                        weaponComponent.getCooldownSeconds()
                )
        );
    }

}
