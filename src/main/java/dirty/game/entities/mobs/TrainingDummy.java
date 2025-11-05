package dirty.game.entities.mobs;

import dirty.game.entities.Destructible;
import dirty.game.items.Item;
import dirty.game.map.collision.WorldGrid;
import dirty.client.appStates.ClientGameAppState;
import static dirty.client.appStates.ClientGameAppState.removeEntityByIdClient;
import dirty.client.ClientSynchronizationUtils;
import dirty.client.Main;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dirty.data.DamageReceiveData;
import dirty.game.entities.Collidable;
import dirty.game.entities.FloatAttribute;
import dirty.game.entities.factories.MobSpawnType;
import dirty.game.map.collision.RectangleAABB;
import lombok.Getter;
import dirty.messages.DestructibleDamageReceiveMessage;
import dirty.messages.NewMobMessage;
import dirty.messages.TwoWayMessage;
import dirty.server.ServerContext;

import static dirty.server.ServerGameAppState.removeEntityByIdServer;

public class TrainingDummy extends Mob {

    protected SkinningControl skinningControl;

    @Getter
    private AnimComposer modelComposer;

    public TrainingDummy(int id, Node node, String name) {
        super(MobSpawnType.TRAINING_DUMMY, id, node, name);
        
        setHealth(100);
        setMaxHealth(100);
        cachedSpeed = 6;
        attributes.put(SPEED_ATTRIBUTE_KEY, new FloatAttribute(cachedSpeed));

        createHitbox();

    }

    @Override
    public void interpolateRotation(float tpf) {
        setRotInterpolationValue(Math.min(rotInterpolationValue + MOB_ROTATION_RATE * tpf, 1));

        node.getLocalRotation().nlerp(ClientSynchronizationUtils.GetYAxisRotation(serverRotation), rotInterpolationValue);
        node.setLocalRotation(node.getLocalRotation());
    }

    @Override
    public void dealDamageClient(float damage, Destructible target) {
        var targetDamageReceiveData = new DamageReceiveData(target.getId(),id,damage);

        for(var onHitEffect : onDealDamageEffects){
            onHitEffect.applyClient(targetDamageReceiveData);
        }

        target.onAttacked(this, targetDamageReceiveData);
    }

    @Override
    public void dealDamageServer(ServerContext ctx, DamageReceiveData damageReceiveData, Destructible target) {
        for(var onHitEffect : onDealDamageEffects){
            onHitEffect.applyServer(ctx,damageReceiveData);
        }

        target.receiveDamageServer(ctx, damageReceiveData);
    }

    @Override
    protected final void createHitbox() {
        float hitboxWidth = 0.5f;
        float hitboxHeight = 1.25f;
        float hitboxLength = 0.5f;
        hitboxNode.move(0, hitboxHeight, 0);
        collisionShape = new RectangleAABB(hitboxNode.getWorldTranslation(), hitboxWidth, hitboxHeight, hitboxLength);
        showHitboxIndicator();
    }

    @Override
    public void onCollisionClient(Collidable other) {
    }

    @Override
    public void onCollisionServer(ServerContext ctx, Collidable other) {
    }

    @Override
    public void onAttacked(Mob shooter, DamageReceiveData damageReceiveData) {
        notifyServerAboutReceivingDamage(damageReceiveData);
    }

    @Override
    public void onInteract() {
        System.out.println("The " + name + " burps!");
    }

    @Override
    public void setPositionClient(Vector3f newPos) {
        setServerLocation(newPos);
        setPosInterpolationValue(1.f);
        WorldGrid grid = ClientGameAppState.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    @Override
    public void setPositionServer(ServerContext ctx, Vector3f newPos) {
        WorldGrid grid = ctx.getCollisionGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
        positionChangedOnServer.set(true);
    }

    @Override
    public TwoWayMessage createNewEntityMessage() {
        NewMobMessage msg = new NewMobMessage(this, node.getWorldTranslation(), mobSpawnType);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void receiveDamageClient(DamageReceiveData damageData) {
        for(var onDamageReceivedEffect : onDamageReceivedEffects){
            onDamageReceivedEffect.applyClient(damageData);
        }
        setHealth(getHealth()-calculateDamage(damageData.getRawDamage()));

        if (getHealth() <= 0) {
            setHealth(1);
        }
    }
    
    @Override
    public void receiveDamageServer(ServerContext ctx, DamageReceiveData damageData) {
        for(var onDamageReceivedEffect : onDamageReceivedEffects){
            onDamageReceivedEffect.applyServer(ctx, damageData);
        }

        setHealth(getHealth()-calculateDamage(damageData.getRawDamage()));

        if (getHealth() <= 0) {
            setHealth(1);
        }
    }


    
    @Override
    public void die() {
        node.removeFromParent();
        ClientGameAppState.getInstance().getGrid().remove(this);
        hideHitboxIndicator();
    }

    @Override
    public float getArmorValue() {
        return 0;
    }

    @Override
    public float calculateDamage(float damage) {
        return damage > 0 ? damage : 0;
    }

    @Override
    public void attack() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void notifyServerAboutReceivingDamage(DamageReceiveData damageReceiveData) {
        DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(damageReceiveData);
        hpUpd.setReliable(true);
        ClientGameAppState.getInstance().getClient().send(hpUpd);
    }

    @Override
    public void equip(Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void unequip(Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void equipServer(ServerContext ctx, Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void unequipServer(ServerContext ctx, Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void moveClient(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void moveServer(ServerContext ctx, Vector3f moveVec) {
        super.moveServer(ctx,moveVec);
        positionChangedOnServer.set(true);
    }

    @Override
    public void destroyServer(ServerContext ctx) {
        removeEntityByIdServer(ctx,id);
        ctx.getCollisionGrid().remove(this);
        if (node.getParent() != null) {
            Main.getInstance().enqueue(() -> {
                node.removeFromParent();
            });
        }
    }

    @Override
    public void destroyClient() {
        var client = ClientGameAppState.getInstance();
        client.getGrid().remove(this);
        Main.getInstance().enqueue(() -> {
            node.removeFromParent();
        });
        removeEntityByIdClient(id);
    }

}
