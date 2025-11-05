package dirty.game.items.armor;

import dirty.game.items.Equippable;
import dirty.game.items.Item;
import com.jme3.scene.Node;
import dirty.itemSystem.ItemTemplates.ItemTemplate;
import lombok.Getter;
import lombok.Setter;

public abstract class Armor extends Item implements Equippable {
    @Getter
    @Setter
    protected float armorValue;

    public Armor(int id, ItemTemplate template,String name,Node node) {
        super(id,template,name,node);
    }

    public Armor(int id, ItemTemplate template,String name,Node node, boolean droppable) {
        super(id,template,name,node,droppable);
    }
}
