package dirty.messages.items;

import dirty.client.appStates.ClientGameAppState;
import dirty.client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import dirty.game.items.Item;
import lombok.Getter;
import dirty.server.ServerContext;

@Serializable
@Getter
public class NewGrenadeMessage extends NewItemMessage {


    public NewGrenadeMessage() {
    }

    public NewGrenadeMessage(Item item) {
        super(item);
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
            Item i = ifa.createItem(id, getTemplate(), droppable);
            if(isAlreadyDropped()){
                Main.getInstance().enqueue(()-> {
                    i.drop(getDroppedPosition());
                });
            }
            client.registerEntity(i);
    }

}
