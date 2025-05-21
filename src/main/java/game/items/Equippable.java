package game.items;

import game.entities.mobs.HumanMob;
import game.entities.mobs.player.Player;
import server.ServerContext;

public interface Equippable {

    public void playerEquipClient(Player m);

    public void playerUnequipClient(Player m);

    public void serverEquip(ServerContext ctx, HumanMob m);

    public void serverUnequip(ServerContext ctx, HumanMob m);

    public void humanMobEquipClient(HumanMob m);

    public void humanMobUnequipClient(HumanMob m);

}
