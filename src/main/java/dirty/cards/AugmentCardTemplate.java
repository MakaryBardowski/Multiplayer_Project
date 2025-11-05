package dirty.cards;

import dirty.game.entities.StatusEffectContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import dirty.server.ServerContext;

@AllArgsConstructor
@Getter
public abstract class AugmentCardTemplate {

    private final int cardId;
    private final String name;
    private final String description;
    private final String iconPath;

    public abstract void chooseCardClient(StatusEffectContainer statusEffectContainer);
    public abstract void chooseCardServer(ServerContext ctx, StatusEffectContainer statusEffectContainer);
}
