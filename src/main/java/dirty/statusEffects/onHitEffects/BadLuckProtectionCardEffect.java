package dirty.statusEffects.onHitEffects;

import dirty.data.DamageReceiveData;
import dirty.game.entities.Destructible;
import dirty.game.entities.StatusEffectContainer;
import dirty.messages.TwoWayMessage;
import dirty.server.ServerContext;
import dirty.statusEffects.EffectProcType;

import java.util.Random;

public class BadLuckProtectionCardEffect extends OnHitEffect{
    private static final float DAMAGE_MULTIPLIER = 1.1f;
    private final Random random = new Random();

    public BadLuckProtectionCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(ServerContext ctx, DamageReceiveData input) {
        var victim = (Destructible) ctx.getEntity(input.getVictimId());

        input.setRawDamage(input.getRawDamage()*DAMAGE_MULTIPLIER);
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
