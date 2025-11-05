package dirty.statusEffects.onDamagedEffects;

import dirty.data.DamageReceiveData;
import dirty.game.entities.StatusEffectContainer;
import dirty.messages.TwoWayMessage;
import dirty.server.ServerContext;
import dirty.statusEffects.EffectProcType;

public class HardnessCardEffect extends OnDamagedEffect {
    private static final float FLAT_DAMAGE_BLOCK = 1f;

    public HardnessCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(ServerContext ctx, DamageReceiveData input) {
        input.setRawDamage(input.getRawDamage()-FLAT_DAMAGE_BLOCK);
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
