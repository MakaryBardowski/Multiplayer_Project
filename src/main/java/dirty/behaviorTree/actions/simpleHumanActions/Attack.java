package dirty.behaviorTree.actions.simpleHumanActions;

import dirty.behaviorTree.NodeAction;
import dirty.behaviorTree.NodeCompletionStatus;
import static dirty.behaviorTree.NodeCompletionStatus.FAILURE;
import dirty.behaviorTree.context.Context;
import dirty.behaviorTree.context.SimpleHumanMobContext;
import dirty.client.Main;
import dirty.game.entities.Destructible;
import dirty.game.entities.mobs.HumanMob;
import dirty.itemSystem.ItemTemplates.MeleeWeaponTemplate;
import dirty.game.items.weapons.MeleeWeapon;

public class Attack extends NodeAction {

    private static final float SECONDS_TO_MILLIS = 1000;
    private long lastAttackedTimestamp = 0;
    private float TEST_GUN_RANGE = 2;
    private float TEST_DAMAGE = 3.75f;
    private HumanMob human;
    private Destructible target;

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (SimpleHumanMobContext) context;
        human = (HumanMob) context.getBlackboard().get(SimpleHumanMobContext.STEERED_HUMAN);
        target = (Destructible) context.getBlackboard().get(SimpleHumanMobContext.TARGET_DESTRUCTIBLE);
//        System.out.println("current gun cooldown before for "+human+" is "+currentGunCooldown);

//        System.out.println("current gun cooldown for "+human+" is "+currentGunCooldown);
        if (human.getEquippedRightHand() instanceof MeleeWeapon mw) { // can be changed to Weapon after i implement ranged weaps

            if (hc.getCurrentUpdateTimestamp() - lastAttackedTimestamp < ((MeleeWeaponTemplate)mw.getTemplate()).getMobUsageData().getCooldown()*SECONDS_TO_MILLIS ) {
                return FAILURE;
            }

            if (target == null) {
                return FAILURE;
            }

            var distance = human.getNode().getWorldTranslation().distance(target.getNode().getWorldTranslation());
            if (distance > TEST_GUN_RANGE) {
                return NodeCompletionStatus.FAILURE;
            }

            Main.getInstance().enqueue(() -> {
                mw.attackServer(hc.getServerContext(),human);
            });
            lastAttackedTimestamp = hc.getCurrentUpdateTimestamp();
        }

//        System.out.println("Attacking!");
        return NodeCompletionStatus.SUCCESS;
    }
}
