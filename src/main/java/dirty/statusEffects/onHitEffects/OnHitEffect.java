package dirty.statusEffects.onHitEffects;

import dirty.data.DamageReceiveData;
import dirty.game.entities.StatusEffectContainer;
import dirty.statusEffects.EffectProcType;
import dirty.statusEffects.StatusEffect;

public abstract class OnHitEffect extends StatusEffect<DamageReceiveData, DamageReceiveData> {
    public OnHitEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

}
