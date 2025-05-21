package game.items.weapons;

import game.entities.mobs.Mob;
import game.entities.mobs.player.Player;
import server.ServerContext;

public interface Attacks {
    void attackServer(ServerContext ctx, Mob m);
    void playerAttackServer(Player p);
}
