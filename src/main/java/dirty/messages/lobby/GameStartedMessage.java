package dirty.messages.lobby;

import dirty.client.appStates.ClientGameAppState;
import dirty.client.Main;
import dirty.client.MainMenuController;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import dirty.messages.TwoWayMessage;
import dirty.server.ServerContext;

@Serializable
public class GameStartedMessage extends TwoWayMessage {

    public GameStartedMessage() {
        setReliable(true);
    }

    public GameStartedMessage(int connectionId, int classId) {
        setReliable(true);
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
    }

    @Override
    public void handleClient(ClientGameAppState clientApp) {
        Main.getInstance().enqueue(() -> {
            MainMenuController.leaveLobby();
            clientApp.joinGame();
        });
    }

}
