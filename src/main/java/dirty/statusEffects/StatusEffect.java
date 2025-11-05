package dirty.statusEffects;

import dirty.game.entities.Destructible;
import dirty.game.entities.Entity;
import dirty.game.entities.StatusEffectContainer;
import lombok.Getter;
import lombok.Setter;
import dirty.server.ServerContext;

public abstract class StatusEffect<InputType,OutputType> extends Entity {
    @Getter
    @Setter
    protected Destructible target;

    protected EffectSource source;

    @Getter
    protected EffectProcType procType;

    @Getter
    @Setter
    protected boolean unique;

    public StatusEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id,name);
        this.target = target;
        this.procType = procType;
    }

    public abstract OutputType applyServer(ServerContext ctx, InputType input);

    public abstract OutputType applyClient(InputType input);

    public abstract boolean shouldBeRemoved();

    protected enum EffectSource {
        BARBED_WIRE_BLEED,
        REGENERATION,
    }

    public int getEffectId() {
        return source.ordinal();
    }

}
