package dirty.statusEffects.temporaryEffects;

import dirty.game.entities.StatusEffectContainer;
import dirty.statusEffects.EffectProcType;
import dirty.statusEffects.StatusEffect;

public abstract class TemporaryEffect extends StatusEffect<Void,Void> {

    protected int ticks = -1; //effect starts immediately
    protected int maxTicks;

    public TemporaryEffect(int id, String name, StatusEffectContainer target, EffectProcType procType, int maxTicks) {
        super(id, name,target, procType);
        this.maxTicks = maxTicks;
    }



}
