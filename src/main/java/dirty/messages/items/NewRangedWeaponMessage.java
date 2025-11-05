package dirty.messages.items;

import dirty.client.appStates.ClientGameAppState;
import dirty.client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import dirty.game.entities.IntegerAttribute;
import dirty.game.items.weapons.RangedWeapon;
import lombok.Getter;
import dirty.server.ServerContext;

@Serializable
@Getter
public class NewRangedWeaponMessage extends NewItemMessage {
    private int ammo;

    public NewRangedWeaponMessage() {
    }

    public NewRangedWeaponMessage(RangedWeapon item) {
        super(item);
        this.ammo = ((IntegerAttribute)item.getAttributes().get(RangedWeapon.AMMO_ATTRIBUTE)).getValue();
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
            RangedWeapon i = (RangedWeapon) ifa.createItem(id, getTemplate(), droppable);
            i.setAmmo(ammo);
            if(isAlreadyDropped()){
                Main.getInstance().enqueue(()-> {
                    i.drop(getDroppedPosition());
                });
            }
            client.registerEntity(i);
    }

}
