package dirty.game.items;

import dirty.game.entities.mobs.HumanMob;
import dirty.game.entities.mobs.player.Player;
import dirty.server.ServerContext;

public interface Equippable {

    public void playerEquipClient(Player m);

    public void playerUnequipClient(Player m);

    public void serverEquip(ServerContext ctx, HumanMob m);

    public void serverUnequip(ServerContext ctx, HumanMob m);

    public void humanMobEquipClient(HumanMob m);

    public void humanMobUnequipClient(HumanMob m);

}
