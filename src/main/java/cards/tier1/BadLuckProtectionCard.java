package cards.tier1;

import Utils.Ansi;
import cards.AugmentCardTemplate;
import game.entities.StatusEffectContainer;
import server.ServerGameAppState;
import statusEffects.EffectTemplates;

public class BadLuckProtectionCard extends AugmentCardTemplate {

    public BadLuckProtectionCard() {
        super(3,"Bad Luck\nProtection", "All damage you deal is \nincreased by \\#FF0000#10\\#FFFFFF#%.","Textures/GUI/Cards/cutdown.png");
    }

    @Override
    public void chooseCardClient(StatusEffectContainer statusEffectContainer) {
        System.out.println(Ansi.withAnsiColor(statusEffectContainer + " chose "+this.getName(),Ansi.BRIGHT_YELLOW));
    }

    @Override
    public void chooseCardServer(StatusEffectContainer statusEffectContainer) {
        var effect = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager().createAndRegisterStatusEffect(EffectTemplates.GOOD_LUCK_PROTECTION,statusEffectContainer);
        statusEffectContainer.addEffect(effect);
    }
}
