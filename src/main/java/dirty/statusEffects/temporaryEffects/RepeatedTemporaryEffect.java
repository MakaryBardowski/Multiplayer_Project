package dirty.statusEffects.temporaryEffects;

import dirty.game.entities.StatusEffectContainer;
import dirty.statusEffects.EffectProcType;

public abstract class RepeatedTemporaryEffect extends TemporaryEffect{
    protected int ticksPerProc;
    
    public RepeatedTemporaryEffect(int id, String name, StatusEffectContainer target, EffectProcType procType, int maxTicks, int ticksPerProc) {
        super(id, name, target, procType,maxTicks);
        this.ticksPerProc = ticksPerProc;
    }
}
