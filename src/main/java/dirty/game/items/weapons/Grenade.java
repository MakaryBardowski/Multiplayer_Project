package dirty.game.items.weapons;

import dirty.FirstPersonHands.FirstPersonHandAnimationData;
import dirty.client.appStates.ClientGameAppState;
import dirty.client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import dirty.game.entities.mobs.HumanMob;
import dirty.game.entities.mobs.Mob;
import dirty.game.entities.mobs.player.Player;
import dirty.itemSystem.ItemTemplates;
import lombok.Getter;
import dirty.messages.GrenadeThrownMessage;
import dirty.messages.TwoWayMessage;
import dirty.messages.items.MobItemInteractionMessage;
import dirty.messages.items.NewGrenadeMessage;
import static dirty.client.appStates.ClientGameAppState.removeEntityByIdClient;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.tween.action.ClipAction;
import de.lessvoid.nifty.controls.label.LabelControl;
import static dirty.game.entities.DestructibleUtils.setupModelShootability;
import dirty.game.items.Holdable;
import dirty.server.ServerContext;

public class Grenade extends ThrowableWeapon {

    private int thirdPersonModelParentIndex;
    private int equipmentPreviewThirdPersonModelParentIndex;

    @Getter
    private final float throwSpeed = 40;

    public Grenade(int id, float damage, ItemTemplates.ItemTemplate template, String name, Node node) {
        super(id, damage, template, name, node);
    }

    public Grenade(int id, float damage, ItemTemplates.ItemTemplate template, String name, Node node, boolean droppable) {
        super(id, damage, template, name, node, droppable);
    }

    @Override
    public void onInteract() {
        ClientGameAppState gs = ClientGameAppState.getInstance();
        MobItemInteractionMessage imsg = new MobItemInteractionMessage(this, gs.getPlayer(), MobItemInteractionMessage.ItemInteractionType.PICK_UP);
        imsg.setReliable(true);
        gs.getClient().send(imsg);
    }

    @Override
    public TwoWayMessage createNewEntityMessage() {
        NewGrenadeMessage msg = new NewGrenadeMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void attackServer(ServerContext ctx, Mob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerAttackServer(Player p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerHoldInRightHand(Player p) {
        AssetManager assetManager = Main.getInstance().getAssetManager();

        p.setEquippedRightHand(this);

        if (playerIsMyPlayer(p)) {
            ClientGameAppState.getInstance().getNifty().getCurrentScreen().findControl("ammo", LabelControl.class).setText("");

            Node model = (Node) assetManager.loadModel(template.getFpPath());

            Geometry ge = (Geometry) model.getChild(0);
            Material originalMaterial = ge.getMaterial();
            Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
            ge.setMaterial(newMaterial);

            p.getFirstPersonHands().attachToHandR(model);
            model.scale(2.f);
            model.move(0.1f, 0.2f, -0.4f);
//            model.move(new Vector3f(0.43199998f, 0.46799996f, -1.6199999f));

            ((ClipAction) p.getFirstPersonHands().getHandsComposer().action("ThrowGrenade")).setTransitionLength(0);

            p.getFirstPersonHands().setHandsAnim(FirstPersonHandAnimationData.HOLD_GRENADE);

            p.getFirstPersonHands().getHandsComposer().setGlobalSpeed(1f);
        }

        humanEquipInThirdPerson(p, assetManager);

    }

    @Override
    public void playerUseInRightHand(Player p) {
        var composer = p.getFirstPersonHands().getHandsComposer();

        Action toIdle = composer.action("KnifeAttackToHold");
        Tween idle = Tweens.callMethod(composer, "setCurrentAction", "HoldKnife");
        composer.actionSequence("ThrowToHold", toIdle, idle);

        Action attack = composer.action("ThrowGrenade");
        Tween attackToIdle = Tweens.callMethod(composer, "setCurrentAction", "ThrowToHold");
        composer.actionSequence("fullSwing", attack, attackToIdle);

        ((ClipAction) composer.action("ThrowGrenade")).setTransitionLength(0);
        composer.setCurrentAction("fullSwing");
        composer.getCurrentAction().setSpeed(1.5f);
        throwGrenade(p);
    }

    private void throwGrenade(Player p) {
//            ServerMain.getInstance().getLevelManagerMobs().forEach((a,b) -> {
//                System.out.println("enitty "+a+"  class "+b.getClass().getSimpleName());
//            });
        var cs = ClientGameAppState.getInstance();
        var grenadeInitialPosition = cs.getCamera().getLocation();
        var throwDirection = cs.getCamera().getDirection().normalize();

        var gtm = new GrenadeThrownMessage(p.getId(), id, grenadeInitialPosition, throwDirection);
        gtm.setReliable(true);

        cs.getClient().send(gtm);

        p.getEquipment().removeItem(this);
        p.unequip(this);
        removeEntityByIdClient(id);

    }

    @Override
    public void playerEquipClient(Player p) {
        Holdable unequippedItem = p.getEquippedRightHand();
        if(unequippedItem == this){
            return;
        }
        if (unequippedItem != null) {
            unequippedItem.playerUnequipClient(p);
        }
        p.setHoldsTrigger(false);
        playerHoldInRightHand(p);
    }

    @Override
    public void playerUnequipClient(Player p) {
        if (p.getEquippedRightHand() != this) {
            return;
        }
        p.setEquippedRightHand(null);
        p.getFirstPersonHands().getRightHandEquipped().detachAllChildren();

        if(ClientGameAppState.getInstance().getCurrentGamemode().getLevelManager().isMyPlayer(p)){
            p.getEquipmentPreviewSkinningControl().getAttachmentsNode("HandR").detachChildAt(equipmentPreviewThirdPersonModelParentIndex);
        }

        p.getSkinningControl().getAttachmentsNode("HandR").detachChildAt(thirdPersonModelParentIndex);
    }

    @Override
    public void serverEquip(ServerContext ctx, HumanMob m) {
        m.setEquippedRightHand(this);
    }

    @Override
    public void serverUnequip(ServerContext ctx, HumanMob m) {
        if(m.getEquippedRightHand() == this) {
            m.setEquippedRightHand(null);
        }
    }

    private boolean playerIsMyPlayer(Player p) {
        return p == ClientGameAppState.getInstance().getPlayer();
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("-Throwable\n");
        builder.append("-Creates smoke screen");
        return builder.toString();
    }

    @Override
    public void humanMobUnequipClient(HumanMob m) {
        if (m.getEquippedRightHand() == this) {
            m.setEquippedRightHand(null);
        }
    }

    @Override
    public void humanMobEquipClient(HumanMob m) {
        Holdable unequippedItem = m.getEquippedRightHand();
        if (unequippedItem != null) {
            unequippedItem.humanMobUnequipClient(m);
        }
        m.setEquippedRightHand(this);
        humanEquipInThirdPerson(m, Main.getInstance().getAssetManager());
    }

    private void humanEquipInThirdPerson(HumanMob humanMob, AssetManager assetManager) {
        Node model = (Node) assetManager.loadModel(template.getDropPath());
        model.move(-0.25f, 0.33f, -0.1f);
        model.rotate(FastMath.DEG_TO_RAD*180,0,0);

        Geometry ge = (Geometry) (model.getChild(0));
        Material originalMaterial = ge.getMaterial();
        Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
        ge.setMaterial(newMaterial);
        float length = 1f;
        float width = 1f;
        float height = 1f;
        model.scale(length, width, height);
        humanMob.getSkinningControl().getAttachmentsNode("HandR").attachChild(model);
        setupModelShootability(model, humanMob.getId());
        thirdPersonModelParentIndex = humanMob.getSkinningControl().getAttachmentsNode("HandR").getChildIndex(model);

        if(humanMob instanceof Player p && p.getEquipmentPreviewNode() != null) {
            var previewModelNode = p.getEquipmentPreviewSkinningControl().getAttachmentsNode("HandR");
            var previewModel = model.clone(false);
            previewModelNode.attachChild(previewModel);
            equipmentPreviewThirdPersonModelParentIndex = p.getEquipmentPreviewSkinningControl().getAttachmentsNode("HandR").getChildIndex(previewModel);
        }
    }
}
