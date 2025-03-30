package game.items.weapons;

import Utils.Triple;
import client.appStates.ClientGameAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import de.lessvoid.nifty.controls.label.LabelControl;
import game.effects.Vfx;
import game.effects.VfxType;
import game.entities.Attribute;
import game.entities.Destructible;
import game.entities.IntegerAttribute;
import game.entities.Entity;
import game.entities.mobs.Mob;
import game.entities.mobs.player.Player;
import itemSystem.ItemTemplates.ItemTemplate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RangedWeapon extends Weapon {

    public static int AMMO_ATTRIBUTE = 2;
    public static int MAX_AMMO_ATTRIBUTE = 3;

    public RangedWeapon(int id, float damage, ItemTemplate template, String name, Node node, int maxAmmo, float roundsPerSecond) {
        super(id, damage, template, name, node, roundsPerSecond);
        attributes.put(AMMO_ATTRIBUTE, new IntegerAttribute(maxAmmo));
        attributes.put(MAX_AMMO_ATTRIBUTE, new IntegerAttribute(maxAmmo));
    }

    public RangedWeapon(int id, float damage, ItemTemplate template, String name, Node node, boolean droppable, int maxAmmo, float roundsPerSecond) {
        super(id, damage, template, name, node, droppable, roundsPerSecond);
        attributes.put(AMMO_ATTRIBUTE, new IntegerAttribute(maxAmmo));
        attributes.put(MAX_AMMO_ATTRIBUTE, new IntegerAttribute(maxAmmo));
    }

    public int getAmmo() {
        return ((IntegerAttribute) attributes.get(AMMO_ATTRIBUTE)).getValue();
    }

    public void setAmmo(int ammo) {
        ((IntegerAttribute) attributes.get(AMMO_ATTRIBUTE)).setValue(ammo);
    }

    public int getMaxAmmo() {
        return ((IntegerAttribute) attributes.get(MAX_AMMO_ATTRIBUTE)).getValue();
    }

    @Override
    public void attributeChangedNotification(int attributeId, Attribute oldAttributeCopy, Attribute copyOfNewAttribute) {
        super.attributeChangedNotification(attributeId,oldAttributeCopy, copyOfNewAttribute); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        if (attributeId == AMMO_ATTRIBUTE || attributeId == MAX_AMMO_ATTRIBUTE) {
            String text = (int) getAmmo() + "/" + (int) getMaxAmmo();
            ClientGameAppState.getInstance().getNifty().getCurrentScreen().findControl("ammo", LabelControl.class).setText(text);
        }
    }

    public abstract float calculateDamage(float distance);

    public abstract void reload(Mob wielder);

    protected final Triple<Vector3f, Vector3f, VfxType> hitscan(Player p) {
        var cs = ClientGameAppState.getInstance();
        CollisionResults results = new CollisionResults();
        Vector3f shotDirection = p.getMainCamera().getDirection();
        Vector3f shotOrigin = p.getMainCamera().getLocation();
        Ray ray = new Ray(shotOrigin, shotDirection);
        float distanceToFirstWall = Float.MAX_VALUE;

        CollisionResult cr = null;
        var hitMob = false;
        if (cs.getMapNode().collideWith(ray, results) > 0) {
            distanceToFirstWall = results.getClosestCollision().getDistance();
            cr = results.getClosestCollision();

        }

        results = new CollisionResults();
        cs.getDestructibleNode().collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();

            float distanceToFirstTarget = closest.getDistance();
            if (distanceToFirstTarget < distanceToFirstWall) {
                cr = closest;

                Integer hitId = Integer.valueOf(closest.getGeometry().getName());
                Entity mobHit = cs.getMobs().get(hitId);
                
                if (mobHit instanceof Destructible destructible) {
                    if(destructible instanceof  Mob){
                        hitMob = true;
                    }
                    p.dealDamageClient(calculateDamage(distanceToFirstTarget),destructible);
                }
            }
        }

        if(!hitMob && cr != null){
            Vfx.getEffect(VfxType.BULLET_IMPACT).playAt(cs.getDebugNode(), cr.getContactPoint().add(cr.getContactNormal().mult(0.3f)),cr.getContactNormal());
        } else if (hitMob){
            Vfx.getEffect(VfxType.ON_SHOT_BLOOD).playAt(cs.getDebugNode(), cr.getContactPoint().add(cr.getContactNormal().mult(0.3f)),cr.getContactNormal());
        }

        return new Triple<>(cr.getContactPoint(), cr.getContactNormal(), hitMob ? VfxType.ON_SHOT_BLOOD : VfxType.BULLET_IMPACT);
    }
}
