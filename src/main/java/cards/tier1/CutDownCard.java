package cards.tier1;

import Utils.Ansi;
import cards.AugmentCardTemplate;
import game.entities.StatusEffectContainer;
import server.ServerContext;
import server.ServerGameAppState;
import statusEffects.EffectTemplates;

public class CutDownCard extends AugmentCardTemplate {

    public CutDownCard() {
        super(0,"Cut Down", "Dealing damage to targets with \nless than \\#FF0000#7.5\\#FFFFFF#% of their max \nhealth instantly kills them.","Textures/GUI/Cards/cutdown.png");
    }

    @Override
    public void chooseCardClient(StatusEffectContainer statusEffectContainer) {
        System.out.println(Ansi.withAnsiColor(statusEffectContainer + " chose "+this.getName(),Ansi.BRIGHT_YELLOW));
    }

    @Override
    public void chooseCardServer(ServerContext ctx, StatusEffectContainer statusEffectContainer) {
        var effect = ctx.getLevelManager().createAndRegisterStatusEffect(EffectTemplates.CUTDOWN_CARD_EXECUTE,statusEffectContainer);
        statusEffectContainer.addEffect(effect);
    }
}
