package dirty.cards.tier1;

import dirty.Utils.Ansi;
import dirty.cards.AugmentCardTemplate;
import dirty.game.entities.StatusEffectContainer;
import dirty.server.ServerContext;
import dirty.statusEffects.EffectTemplates;

public class HardnessCard extends AugmentCardTemplate {

    public HardnessCard() {
        super(2,"Hardness", "Reduce all incoming damage by\n\\#FF0000#1\\#FFFFFF#.","Textures/GUI/Cards/regeneration.png");
    }

    @Override
    public void chooseCardClient(StatusEffectContainer statusEffectContainer) {
        System.out.println(Ansi.withAnsiColor(statusEffectContainer + " chose "+this.getName(),Ansi.BRIGHT_YELLOW));
    }

    @Override
    public void chooseCardServer(ServerContext ctx, StatusEffectContainer statusEffectContainer) {
        var effect = ctx.getLevelManager().createAndRegisterStatusEffect(EffectTemplates.HARDNESS_CARD_ARMOR,statusEffectContainer);
        statusEffectContainer.addEffect(effect);
    }
}
