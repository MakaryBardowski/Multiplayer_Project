package dirty.statusEffects.onHitEffects;

import dirty.data.DamageReceiveData;
import dirty.game.entities.Destructible;
import dirty.game.entities.StatusEffectContainer;
import dirty.messages.TwoWayMessage;
import dirty.server.ServerContext;
import dirty.statusEffects.EffectProcType;

public class CutDownCardEffect extends OnHitEffect{
    private float EXECUTE_THRESHOLD = 7.5f/100;

    public CutDownCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(ServerContext ctx, DamageReceiveData input) {
        var attacker = (Destructible) ctx.getEntity(input.getAttackerId());

        var victim = (Destructible) ctx.getEntity(input.getVictimId());

        if( ( victim.getHealth() - victim.calculateDamage(input.getRawDamage()) ) <= victim.getMaxHealth()*EXECUTE_THRESHOLD){
            input.setRawDamage(victim.getMaxHealth()*99);
        }
        return input;
    }

    @Override
    public DamageReceiveData applyClient(DamageReceiveData input) {
        return null;
    }

    @Override
    public boolean shouldBeRemoved() {
        return false;
    }

    @Override
    public TwoWayMessage createNewEntityMessage() {
        return null;
    }
}
