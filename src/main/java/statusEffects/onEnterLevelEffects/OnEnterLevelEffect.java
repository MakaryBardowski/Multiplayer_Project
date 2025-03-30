package statusEffects.onEnterLevelEffects;

import data.DamageReceiveData;
import game.entities.StatusEffectContainer;
import statusEffects.EffectProcType;
import statusEffects.StatusEffect;

public abstract class OnEnterLevelEffect extends StatusEffect<Void,Void> {
    public OnEnterLevelEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }
}
