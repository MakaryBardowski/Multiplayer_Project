package statusEffects.temporaryEffects;

import game.entities.Destructible;
import game.entities.StatusEffectContainer;
import game.map.collision.WorldGrid;
import lombok.Getter;
import lombok.Setter;
import messages.DestructibleDamageReceiveMessage;
import messages.TwoWayMessage;
import server.ServerContext;
import server.ServerGameAppState;
import statusEffects.EffectProcType;

public class DamageOverTimeEffect extends RepeatedTemporaryEffect {

    @Getter
    @Setter
    private Destructible target;
    
    @Getter
    private final float damage;

    public DamageOverTimeEffect(int id, String name, StatusEffectContainer target, EffectProcType procType, int maxTicks, int activationsOverTime, float damage) {
        super(id, name, target, procType, maxTicks, activationsOverTime);
        this.damage = damage;
        unique = true;
        source = EffectSource.BARBED_WIRE_BLEED;
    }

    @Override
    public Void applyClient(Void input) {
        return null;
    }

    @Override
    public boolean shouldBeRemoved() {
        return maxTicks > 0 && ticks > maxTicks;
    }

    @Override
    public Void applyServer(ServerContext ctx, Void input) {
        ticks++;

        if (ticks % (ticksPerProc) == 0) {

            target.setHealth(target.getHealth() - target.calculateDamage(damage));

            if (target.getHealth() <= 0) {
                ctx.getCollisionGrid().remove(target);
                ctx.removeEntity(target.getId());

            }

            DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(target.getId(),target.getId(), damage);
            hpUpd.setReliable(true);
            ctx.notifyAllClients(hpUpd);
        }

        return null;
    }

    @Override
    public TwoWayMessage createNewEntityMessage() {
        return null;
    }
}
