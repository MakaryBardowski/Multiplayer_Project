package dirty.statusEffects.onDamagedEffects;

import dirty.data.DamageReceiveData;
import dirty.game.entities.StatusEffectContainer;
import dirty.statusEffects.EffectProcType;
import dirty.statusEffects.StatusEffect;

public abstract class OnDamagedEffect extends StatusEffect<DamageReceiveData,DamageReceiveData> {
    public OnDamagedEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }
}
