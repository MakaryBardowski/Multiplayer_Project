package dirty.messages;

import dirty.client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import dirty.game.entities.Entity;
import lombok.Getter;
import dirty.server.ServerContext;

import java.util.Map;

@Serializable
public class BatchSetFloatAttributeMessage extends TwoWayMessage {

    @Getter
    private int entityId;

    @Getter
    private Map<Integer,Float> attributesByAttributeId;

    public BatchSetFloatAttributeMessage() {
        setReliable(true);
    }

    public BatchSetFloatAttributeMessage(Entity entity, Map<Integer,Float> attributesByAttributeId) {
        this.entityId = entity.getId();
        this.attributesByAttributeId = attributesByAttributeId;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
        var entity = ctx.getEntity(entityId);
        entity.setFloatAttributesAndNotifyClients(ctx,attributesByAttributeId);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = client.getMobs().get(entityId);
        entity.setFloatAttributes(attributesByAttributeId);
    }

}
