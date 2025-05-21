package messages;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import game.entities.Entity;
import game.entities.grenades.ClientThrownGrenadeRotateControl;
import game.entities.grenades.ServerThrownGrenadeControl;
import game.entities.grenades.ThrownGrenade;
import game.entities.grenades.ThrownSmokeGrenade;
import game.entities.mobs.HumanMob;
import itemSystem.ItemTemplates;
import game.items.weapons.Grenade;
import lombok.Getter;
import lombok.Setter;
import messages.items.MobItemInteractionMessage;
import static messages.messageListeners.ServerMessageListener.enqueueExecutionServer;

import server.ServerContext;
import server.ServerGameAppState;
import static server.ServerGameAppState.removeEntityByIdServer;

@Serializable
public class GrenadeThrownMessage extends TwoWayMessage {

    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private int throwingMobId;

    private float posX;
    private float posY;
    private float posZ;

    private float dirX;
    private float dirY;
    private float dirZ;

    public GrenadeThrownMessage() {
    }

    // if sent  client -> server it contains the data about the grenade item the thrown grenade originates from
    // if sent  server -> client it contains the data about the new thrown grenade and its position
    public GrenadeThrownMessage(int throwingMobId, int grenadeItemId, Vector3f pos, Vector3f throwDirection) {
        this.throwingMobId = throwingMobId;
        this.id = grenadeItemId;
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.posZ = pos.getZ();

        this.dirX = throwDirection.getX();
        this.dirY = throwDirection.getY();
        this.dirZ = throwDirection.getZ();

    }

    public Vector3f getPos() {
        return new Vector3f(posX, posY, posZ);
    }

    public Vector3f getDirection() {
        return new Vector3f(dirX, dirY, dirZ);
    }

    @Override
    public void handleServer(ServerContext ctx, HostedConnection hc) {
        ThrownGrenade thrownGrenade = new ThrownSmokeGrenade(Entity.NOT_DETERMINED_ID, "Thrown Smoke Grenade", new Node());
        
        Grenade originGrenade = ((Grenade) getEntityByIdServer(ctx,id));
        float speed = originGrenade.getThrowSpeed();
        Node gnode = thrownGrenade.getNode();
        Node root = ctx.getRootNode();
        var grenadeControl = new ServerThrownGrenadeControl(ctx,thrownGrenade, getDirection(), speed);

        ((HumanMob) ctx.getEntity(throwingMobId)).unequipServer(ctx,
                originGrenade
        );

        enqueueExecutionServer(() -> {
            thrownGrenade.getNode().move(getPos());
            root.attachChild(gnode);
            gnode.addControl(grenadeControl);
        });

        ServerGameAppState.removeItemFromMobEquipmentServer(ctx,throwingMobId, id);
        removeEntityByIdServer(ctx,originGrenade.getId());
        ctx.addEntity(thrownGrenade);

        MobItemInteractionMessage imsg = new MobItemInteractionMessage(originGrenade.getId(), throwingMobId, MobItemInteractionMessage.ItemInteractionType.DESTROY);
        imsg.setReliable(true);
        ctx.notifyAllClients(imsg);

        GrenadeThrownMessage msg = new GrenadeThrownMessage(throwingMobId, thrownGrenade.getId(), getPos(), getDirection());
        msg.setReliable(true);
        ctx.notifyAllClients(msg);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        Main.getInstance().enqueue(() -> {
            Node model = (Node) client.getAssetManager().loadModel(ItemTemplates.SMOKE_GRENADE.getDropPath());
            client.getDebugNode().attachChild(model);
            model.setLocalTranslation(getPos());
            model.scale(1f);


        System.out.println("vlient thrown grenade id "+id);


            Geometry ge = (Geometry) model.getChild(0);
            Material originalMaterial = ge.getMaterial();
            Material newMaterial = new Material(client.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
            newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
            ge.setMaterial(newMaterial);
            var rotControl = new ClientThrownGrenadeRotateControl();
            ge.addControl(rotControl);

            ThrownGrenade g = new ThrownSmokeGrenade(id, "Thrown Smoke", model);
            client.getMobs().put(g.getId(), g);

        });
    }

}
