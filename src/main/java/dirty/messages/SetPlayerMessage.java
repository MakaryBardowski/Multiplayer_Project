package dirty.messages;

import dirty.game.items.weapons.NewGunRecoilControl;
import dirty.guiComponents.LemurPlayerInventoryGui;
import dirty.guiComponents.LemurPlayerHealthbar;
import dirty.client.appStates.ClientGameAppState;
import dirty.client.PlayerHUD;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dirty.game.cameraAndInput.InputController;
import dirty.game.entities.mobs.player.Player;
import lombok.Getter;
import dirty.server.ServerContext;

@Serializable
public class SetPlayerMessage extends TwoWayMessage {

    @Getter
    private int id;
    private float x;
    private float y;
    private float z;
    @Getter
    private String name;

    @Getter
    private int classIndex;

    public SetPlayerMessage() {
    }

    public SetPlayerMessage(int id, Vector3f pos, String name, int classIndex) {
        this.id = id;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.name = name;
        this.classIndex = classIndex;
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        addMyPlayer(this);
    }

    private void addMyPlayer(SetPlayerMessage nmsg) {
        enqueueExecution(() -> {
            createMyPlayer(nmsg);
        });
    }

    private void createMyPlayer(SetPlayerMessage nmsg) {
        Player p = registerMyPlayer(nmsg);

        p.setCameraControl(new NewGunRecoilControl(
                -3,
                3,
                0,
                20,
                0
        ));

        ClientGameAppState.getInstance().setPlayer(p);
        placeMyPlayer(nmsg, p);
        addInputListeners();
        addPlayerHUD(p);
        p.setName(nmsg.getName());
    }

    private void placeMyPlayer(SetPlayerMessage nmsg, Player p) {
        Node playerNode = p.getNode();
        ClientGameAppState.getInstance().getEntityNode().attachChild(playerNode);
        playerNode.setCullHint(Spatial.CullHint.Always);
        p.getNode().setLocalTranslation(nmsg.getPos());
        ClientGameAppState.getInstance().getGrid().insert(p);
    }

    private void addInputListeners() {
        var inputController = new InputController();
        inputController.createInputListeners(ClientGameAppState.getInstance());
        ClientGameAppState.getInstance().setInputController(inputController);
    }

    private void addPlayerHUD(Player player) {
        var LemurPlayerHud = new LemurPlayerHealthbar(player);
        var LemurPlayerEquipment = new LemurPlayerInventoryGui(player);

        player.setPlayerHealthbar(LemurPlayerHud);
        player.setPlayerinventoryGui(LemurPlayerEquipment);;
        ClientGameAppState.getInstance().getStateManager().attach(new PlayerHUD(ClientGameAppState.getInstance()));
    }

    private Player registerMyPlayer(SetPlayerMessage nmsg) {
        return ClientGameAppState.getInstance().registerPlayer(nmsg.getId(), true, classIndex);
    }

}
