package game.entities.mobs;

import game.items.Item;
import server.ServerContext;

public interface MobInterface extends AiSteerable{

    
    void attack();

    void equip(Item e);

    void unequip(Item e);

    void equipServer(ServerContext ctx, Item e);
    
    void unequipServer(ServerContext ctx, Item e);
    
    
}
