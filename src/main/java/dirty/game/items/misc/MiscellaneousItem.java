package dirty.game.items.misc;

import dirty.client.appStates.ClientGameAppState;
import com.jme3.scene.Node;
import dirty.game.items.Item;
import dirty.itemSystem.ItemTemplates;
import dirty.messages.TwoWayMessage;
import dirty.messages.items.MobItemInteractionMessage;
import dirty.messages.items.NewMiscItemMessage;

public class MiscellaneousItem extends Item {
    public MiscellaneousItem(int id, ItemTemplates.ItemTemplate template, String name, Node node) {
        super(id, template, name, node);
    }

    public MiscellaneousItem(int id, ItemTemplates.ItemTemplate template, String name, Node node, boolean droppable) {
        super(id, template, name, node, droppable);
    }

    @Override
    public void onInteract() {
            ClientGameAppState gs = ClientGameAppState.getInstance();
            MobItemInteractionMessage imsg = new MobItemInteractionMessage(this, gs.getPlayer(), MobItemInteractionMessage.ItemInteractionType.PICK_UP);
            imsg.setReliable(true);
            gs.getClient().send(imsg);
    }

    @Override
    public TwoWayMessage createNewEntityMessage() {
        var msg = new NewMiscItemMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public String getDescription() {
        return template.getDescription();
    }
}
