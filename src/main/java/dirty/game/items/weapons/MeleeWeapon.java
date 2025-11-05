package dirty.game.items.weapons;

import com.jme3.anim.AnimComposer;
import com.jme3.scene.Node;
import dirty.itemSystem.ItemTemplates.ItemTemplate;
import dirty.game.entities.mobs.Mob;
import dirty.game.entities.mobs.player.Player;

public abstract class MeleeWeapon extends Weapon {

    protected AnimComposer composer;
    protected SlashControl slashControl;

    public MeleeWeapon(int id, float damage, ItemTemplate template, String name, Node node, float attacksPerSec) {
        super(id, damage, template, name, node, attacksPerSec);
    }

    public MeleeWeapon(int id, float damage, ItemTemplate template, String name, Node node, boolean droppable, float attacksPerSec) {
        super(id, damage, template, name, node, droppable, attacksPerSec);
    }

    public abstract void slashPlayer(Player wielder);

    public abstract void slashMob(Mob wielder);
    

}
