package statusEffects.onHitEffects;

import com.jme3.network.AbstractMessage;
import data.DamageReceiveData;
import game.entities.Destructible;
import game.entities.StatusEffectContainer;
import server.ServerGameAppState;
import statusEffects.EffectProcType;

import java.util.Random;

public class BadLuckProtectionCardEffect extends OnHitEffect{
    private static final float DAMAGE_MULTIPLIER = 1.1f;
    private final Random random = new Random();

    public BadLuckProtectionCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(DamageReceiveData input) {
        var serverLevelManager = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager();
        var victim = (Destructible) serverLevelManager.getEntitiesById().get(input.getVictimId());

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
    public AbstractMessage createNewEntityMessage() {
        return null;
    }
}
