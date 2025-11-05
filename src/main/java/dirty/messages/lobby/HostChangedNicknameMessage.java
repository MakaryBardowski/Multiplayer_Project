package dirty.messages.lobby;

import dirty.client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import dirty.messages.TwoWayMessage;
import static dirty.messages.lobby.HostJoinedLobbyMessage.updateLobby;

import dirty.server.ServerContext;

@Serializable
public class HostChangedNicknameMessage extends TwoWayMessage {

    private int connectionId;
    private String nick;


    public HostChangedNicknameMessage() {
        setReliable(true);
    }

    public HostChangedNicknameMessage(int connectionId, String nick) {
        this.connectionId = connectionId;
        this.nick = nick;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
        var newHc = ctx.getConnectedClient(connectionId);
        
        newHc.setAttribute("nick", nick);
//        System.out.println("connection"+connectionId+" new name "+newHc.getAttribute("nick"));
        ctx.notifyAllClients(this);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        updateLobby(connectionId,nick);
    }


}
