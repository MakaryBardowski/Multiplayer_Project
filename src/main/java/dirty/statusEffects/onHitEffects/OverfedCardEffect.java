package dirty.statusEffects.onHitEffects;

import dirty.data.DamageReceiveData;
import dirty.game.entities.Destructible;
import dirty.game.entities.StatusEffectContainer;
import dirty.messages.TwoWayMessage;
import dirty.server.ServerContext;
import dirty.statusEffects.EffectProcType;

import java.util.HashMap;
import java.util.Map;

public class OverfedCardEffect extends OnHitEffect{
    private static final float HEALTH_INCREASE = .5f;

    public OverfedCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(ServerContext ctx, DamageReceiveData input) {
        var attacker = (Destructible) ctx.getEntity(input.getAttackerId());
        var victim = (Destructible) ctx.getEntity(input.getVictimId());

        if( ( victim.getHealth() - victim.calculateDamage(input.getRawDamage()) ) <= 0){
            attacker.setFloatAttributesAndNotifyClients(ctx,getHealthAndMaxHealthUpdate(
                    attacker.getFloatAttribute(Destructible.MAX_HEALTH_ATTRIBUTE_KEY).getValue()+HEALTH_INCREASE,
                    attacker.getFloatAttribute(Destructible.HEALTH_ATTRIBUTE_KEY).getValue()+HEALTH_INCREASE
            ));
        }
        return input;
    }

    private Map<Integer,Float> getHealthAndMaxHealthUpdate(float newMaxHealthValue, float newHealthValue){
        var updates = new HashMap<Integer,Float>();
        updates.put(Destructible.MAX_HEALTH_ATTRIBUTE_KEY,newMaxHealthValue);
        updates.put(Destructible.HEALTH_ATTRIBUTE_KEY,newHealthValue);
        return updates;
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
