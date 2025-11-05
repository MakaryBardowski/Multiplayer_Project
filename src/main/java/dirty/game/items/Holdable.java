package dirty.game.items;

import dirty.game.entities.mobs.player.Player;

public interface Holdable extends Equippable{
    void playerHoldInRightHand(Player p);
    void playerUseInRightHand(Player p);
}