package messages.items;

import client.appStates.ClientGameAppState;
import static client.appStates.ClientGameAppState.removeEntityByIdClient;
import client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.mobs.Mob;
import game.entities.mobs.player.Player;
import game.items.Item;
import lombok.Getter;
import messages.TwoWayMessage;
import server.ServerContext;
import server.ServerGameAppState;
import static server.ServerGameAppState.removeItemFromMobEquipmentServer;

@Serializable
public class MobItemInteractionMessage extends TwoWayMessage {

    @Getter
    protected int itemId;

    @Getter
    protected int mobId;

    protected int interactionTypeIndex;

    public MobItemInteractionMessage() {
        this.setReliable(true);
    }

    public MobItemInteractionMessage(Item item, Mob mob, ItemInteractionType type) {
        this(item.getId(), mob.getId(), type);
        this.setReliable(true);
    }

    public MobItemInteractionMessage(int itemId, int mobId, ItemInteractionType type) {
        this.itemId = itemId;
        this.mobId = mobId;
        this.interactionTypeIndex = type.ordinal();
        this.setReliable(true);
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
        if(getMobByIdServer(ctx,mobId) == null){
            System.err.println("Provided mob with id "+mobId + " doesnt exist on server. It requested item interaction "+getInteractionType());
            return;
        }
        if(getItemByIdServer(ctx,itemId) == null){
            System.err.println("Provided item with id "+itemId + " doesnt exist on server. It was a target of item interaction "+getInteractionType());
            return;
        }

        if (getInteractionType() == ItemInteractionType.PICK_UP) {
            var mob = getMobByIdServer(ctx,mobId);
            var newItem = getItemByIdServer(ctx,itemId);

            if (mob.getEquipment().addItem(newItem)) {
                var onPickupHandler = newItem.getTemplate().getServerOnPickupHandler();
                if(onPickupHandler != null){
                    onPickupHandler.accept(newItem,mob);
                }

                ctx.notifyAllClients(this);
            }
        } else if (getInteractionType() == ItemInteractionType.EQUIP) {
            var mob = getMobByIdServer(ctx,mobId);
            var item = getItemByIdServer(ctx,itemId);
            if(mob.getEquipment().hasItem(item)){
                mob.equipServer(ctx,getItemByIdServer(ctx,itemId));
                ctx.notifyAllClients(this);
            }
        } else if (getInteractionType() == ItemInteractionType.UNEQUIP) {
//            getMobById(imsg.getMobId()).unequip(getItemById(imsg.getItemId()));
            ctx.notifyAllClients(this);
        } else if (getInteractionType() == ItemInteractionType.DROP) {
            var mob = getMobByIdServer(ctx,mobId);
            var droppedItem = getItemByIdServer(ctx,itemId);

            removeItemFromMobEquipmentServer(ctx,mobId, itemId);
            mob.unequipServer(ctx,droppedItem);

            var onDropHandler = droppedItem.getTemplate().getServerOnDropHandler();
            if(onDropHandler != null){
                onDropHandler.accept(droppedItem,mob);
            }

            ctx.notifyAllClients(this);
        }
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        Main.getInstance().enqueue(() -> {
            if(getMobByIdClient(mobId) == null){
                System.err.println("Provided mob with id "+mobId + " doesnt exist on client. It requested item interaction "+getInteractionType());
                return;
            }
            Item targetItem = getItemByIdClient(itemId);
            if(targetItem == null){
                System.err.println("Provided item with id "+itemId + " doesnt exist on client. It was a target of item interaction "+getInteractionType());
                return;
            }

            switch (getInteractionType()) {
                case EQUIP:
                    getMobByIdClient(mobId).equip(targetItem);
                    break;
                case UNEQUIP:
                    getMobByIdClient(mobId).unequip(targetItem);
                    break;
                case PICK_UP:
                    if (targetItem.getDroppedItemNode() != null) {
                        Main.getInstance().enqueue(() -> {
                            targetItem.getDroppedItemNode().removeFromParent();
                        });
                    }
                    getMobByIdClient(mobId).getEquipment().addItem(targetItem);
                    break;
                case DROP:
                    var mobDroppingItem = getMobByIdClient(mobId);
                    removeItemFromMobEquipmentClient(mobId, itemId);
                    removeItemFromPlayerHotbar(client.getPlayer(),targetItem);
                    mobDroppingItem.unequip(targetItem);
                    Item dropped = getItemByIdClient(itemId);
                    dropped.drop(mobDroppingItem.getNode().getWorldTranslation().add(0, 2, 0), mobDroppingItem.getNode().getLocalRotation().getRotationColumn(2).normalize().multLocal(8));
                    break;
                case DESTROY:
                    removeItemFromPlayerHotbar(client.getPlayer(),targetItem);
                    removeItemFromMobEquipmentClient(mobId, itemId);
                    removeEntityByIdClient(itemId);
                    break;
                default:
                    break;
            }
        });
    }

    public enum ItemInteractionType {
        PICK_UP,
        DROP,
        EQUIP,
        UNEQUIP,
        DESTROY
    }

    public ItemInteractionType getInteractionType() {
        return ItemInteractionType.values()[interactionTypeIndex];
    }

    public void removeItemFromPlayerHotbar(Player player, Item targetItem){
        if(player != null && mobId == player.getId() && player.getPlayerinventoryGui() != null){
            player.getHotbar().removeItem(targetItem);
        }
    }
}
