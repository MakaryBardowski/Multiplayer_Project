package cards.tier1;

import Utils.Ansi;
import cards.AugmentCardTemplate;
import game.entities.StatusEffectContainer;
import server.ServerContext;
import server.ServerGameAppState;
import statusEffects.EffectTemplates;

public class OverfedCard extends AugmentCardTemplate {

    public OverfedCard() {
        super(4,"Overfed", "Killing targets permanently \ngrants you \\#FF0000#0.5\\#FFFFFF# \nmaximum health.","Textures/GUI/Cards/regeneration.png");
    }

    @Override
    public void chooseCardClient(StatusEffectContainer statusEffectContainer) {
        System.out.println(Ansi.withAnsiColor(statusEffectContainer + " chose "+this.getName(),Ansi.BRIGHT_YELLOW));
    }

    @Override
    public void chooseCardServer(ServerContext ctx, StatusEffectContainer statusEffectContainer) {
        var effect = ctx.getLevelManager().createAndRegisterStatusEffect(EffectTemplates.OVERFED_HEALTH_STACK,statusEffectContainer);
        statusEffectContainer.addEffect(effect);
    }
}
