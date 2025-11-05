package dirty.messages.items;

import dirty.client.appStates.ClientGameAppState;
import dirty.client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import dirty.game.items.Item;
import dirty.server.ServerContext;

@Serializable
public class NewMiscItemMessage extends NewItemMessage{

    public NewMiscItemMessage() {
    }

    public NewMiscItemMessage(Item item) {
        super(item);
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection sender) {
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        Item i = (Item) ifa.createItem(id, getTemplate(), droppable);
        if(isAlreadyDropped()){
            Main.getInstance().enqueue(()-> {
                i.drop(getDroppedPosition());
            });
        }
        client.registerEntity(i);
    }
}
