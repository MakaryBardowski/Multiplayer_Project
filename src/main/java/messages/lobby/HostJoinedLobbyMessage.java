package messages.lobby;

import client.appStates.ClientGameAppState;
import client.appStates.LobbyTeamViewAppState;
import client.appStates.MainMenuAppState;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import de.lessvoid.nifty.controls.Label;
import lombok.Setter;
import messages.TwoWayMessage;
import server.ServerContext;
import server.ServerGameAppState;

@Serializable
public class HostJoinedLobbyMessage extends TwoWayMessage {

    private String nick;
    private int connectionId;

    @Setter
    private boolean host;

    public HostJoinedLobbyMessage() {
        setReliable(true);
    }

    public HostJoinedLobbyMessage(int connectionId, String nick) {
        this.connectionId = connectionId;
        this.nick = nick;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection senderHc) {
        var newHc = ctx.getConnectedClient(connectionId);
        if (ctx.getConnectedClients().size() >= 5) {
            newHc.close("Lobby is full.");
        }

        if (ctx.getConnectedClients().size() == 1) {
            newHc.setAttribute("isHost", true);
            setHost(true);
        } else {
            newHc.setAttribute("isHost", false);
        }
        newHc.setAttribute("nick", nick);

        ctx.notifySelectClients(this,Filters.notEqualTo(newHc));

        ctx.getConnectedClients().forEach(hc -> {
            String playerNick = (String) hc.getAttribute("nick");
//            System.out.println("sending "+connectionId+" nick => "+playerNick);
            var msg = new HostJoinedLobbyMessage(hc.getId(), playerNick);
            boolean isHost = (boolean) hc.getAttribute("isHost");
            msg.setHost(isHost);
            ctx.notifySelectClients(msg, Filters.in(newHc));
        });
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        updateLobby(connectionId, nick);
    }

    public static void updateLobby(int connectionId, String nick) {
        var nifty = MainMenuAppState.getNifty();
        Label textField = nifty.getCurrentScreen().findNiftyControl("playerSlot" + connectionId, Label.class);
        if (textField == null) {
            return;
        }
        textField.setText(nick);

        if (connectionId == MainMenuAppState.getClient().getClient().getId()) {
            LobbyTeamViewAppState.setCurrentNickname(nick);
        }
    }

}
