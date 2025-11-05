package dirty.messages;

import dirty.client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import dirty.game.entities.Entity;
import lombok.Getter;
import dirty.server.ServerContext;

import java.util.Map;

@Serializable
public class BatchSetIntegerAttributeMessage extends TwoWayMessage {

    @Getter
    private int entityId;

    @Getter
    private Map<Integer,Integer> attributesByAttributeId;

    public BatchSetIntegerAttributeMessage() {
        setReliable(true);
    }

    public BatchSetIntegerAttributeMessage(Entity entity, Map<Integer,Integer> attributesByAttributeId) {
        this.entityId = entity.getId();
        this.attributesByAttributeId = attributesByAttributeId;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
        var entity = ctx.getEntity(entityId);
        entity.setIntegerAttributesAndNotifyClients(ctx,attributesByAttributeId);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = client.getMobs().get(entityId);
        entity.setIntegerAttributes(attributesByAttributeId);
    }

}
