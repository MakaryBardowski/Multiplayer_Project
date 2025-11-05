package dirty.statusEffects.temporaryEffects;

import dirty.game.entities.StatusEffectContainer;
import lombok.Getter;
import dirty.messages.DestructibleHealReceiveMessage;
import dirty.messages.TwoWayMessage;
import dirty.server.ServerContext;
import dirty.statusEffects.EffectProcType;

public class HealOverTimeEffect extends RepeatedTemporaryEffect {

    @Getter
    private final float heal;

    public HealOverTimeEffect(int id, String name, StatusEffectContainer target, EffectProcType procType, int maxTicks, int activationsOverTime, float heal) {
        super(id, name, target, procType, maxTicks, activationsOverTime);
        this.heal = heal;
        unique = true;
        source = EffectSource.REGENERATION;
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
            if(maxTicks <= 0) // if infinite effect, loop
                ticks = 0;
            
            var healingDone = Math.min(heal, target.getMaxHealth() - target.getHealth());
            target.setHealth(target.getHealth() + healingDone);

            DestructibleHealReceiveMessage hpUpd = new DestructibleHealReceiveMessage(target.getId(), healingDone);
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
