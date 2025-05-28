package messages.lobby;

import client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import messages.TwoWayMessage;
import server.ServerContext;

@Serializable
public class HostChangedPlayerClassMessage extends TwoWayMessage {

    private int connectionId;
    private int classId;


    public HostChangedPlayerClassMessage() {
        setReliable(true);
    }

    public HostChangedPlayerClassMessage(int connectionId, int classId) {
        this.connectionId = connectionId;
        this.classId = classId;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
        var newHc = ctx.getConnectedClient(connectionId);
        
        newHc.setAttribute("class", classId);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
    }


}
