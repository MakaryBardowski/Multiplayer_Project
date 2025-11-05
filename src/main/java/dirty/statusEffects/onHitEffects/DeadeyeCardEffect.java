package dirty.statusEffects.onHitEffects;

import dirty.data.DamageReceiveData;
import dirty.game.constants.SharedConstants;
import dirty.game.entities.Destructible;
import dirty.game.entities.StatusEffectContainer;
import dirty.messages.TwoWayMessage;
import dirty.server.ServerContext;
import dirty.statusEffects.EffectProcType;

import java.util.Random;

public class DeadeyeCardEffect extends OnHitEffect{
    private float CRIT_CHANCE = 0.03f;
    private float PERCENT_CHANCE_PER_TILE = 1;
    private static float CRIT_DAMAGE_MULTIPLIER = 2f;

    private Random random = new Random();

    public DeadeyeCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(ServerContext ctx, DamageReceiveData input) {
        var attacker = (Destructible) ctx.getEntity(input.getAttackerId());
        var victim = (Destructible) ctx.getEntity(input.getVictimId());

        // additional 1 percent per tile
        var additionalCritChanceForDistance = (
                (int)(attacker.getNode().getWorldTranslation().distance(victim.getNode().getWorldTranslation()) / SharedConstants.BLOCK_SIZE)
        )*(PERCENT_CHANCE_PER_TILE/100);
        var totalChance = CRIT_CHANCE + additionalCritChanceForDistance;

//        System.out.println("total chance is "+totalChance);
        if(random.nextFloat() <= totalChance){
            input.setRawDamage(input.getRawDamage() * CRIT_DAMAGE_MULTIPLIER);
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
