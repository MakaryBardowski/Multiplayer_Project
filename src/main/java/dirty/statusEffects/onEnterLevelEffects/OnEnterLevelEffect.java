package dirty.statusEffects.onEnterLevelEffects;

import dirty.game.entities.StatusEffectContainer;
import dirty.statusEffects.EffectProcType;
import dirty.statusEffects.StatusEffect;

public abstract class OnEnterLevelEffect extends StatusEffect<Void,Void> {
    public OnEnterLevelEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }
}
