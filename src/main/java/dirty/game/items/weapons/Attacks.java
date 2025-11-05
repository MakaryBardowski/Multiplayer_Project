package dirty.game.items.weapons;

import dirty.game.entities.mobs.Mob;
import dirty.game.entities.mobs.player.Player;
import dirty.server.ServerContext;

public interface Attacks {
    void attackServer(ServerContext ctx, Mob m);
    void playerAttackServer(Player p);
}
