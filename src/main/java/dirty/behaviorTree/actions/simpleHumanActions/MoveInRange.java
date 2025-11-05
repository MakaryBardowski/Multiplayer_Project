package dirty.behaviorTree.actions.simpleHumanActions;

import dirty.behaviorTree.NodeAction;
import dirty.behaviorTree.NodeCompletionStatus;
import static dirty.behaviorTree.NodeCompletionStatus.STOP_TREE_EXECUTION;
import dirty.behaviorTree.context.Context;
import dirty.behaviorTree.context.SimpleHumanMobContext;
import dirty.client.Main;
import com.jme3.math.Vector3f;
import dirty.game.entities.Destructible;
import dirty.game.entities.mobs.HumanMob;
import dirty.server.ServerGameAppState;

public class MoveInRange extends NodeAction {

    private float TEST_WEAPON_RANGE = 1.5f-0.1f;
    private Vector3f moveVec;
    private Destructible target;
    private HumanMob human;
    private SimpleHumanMobContext hc;
    private Vector3f direction;

    @Override
    public NodeCompletionStatus execute(Context context) {
        hc = (SimpleHumanMobContext) context;
        human = hc.getHumanMob();

        if (human.isDead()) {
            return STOP_TREE_EXECUTION;
        }

        target = hc.getCurrentTarget();
        direction = target.getNode().getWorldTranslation()
                .subtract(human.getNode().getWorldTranslation());
        var moveDistance = human.getCachedSpeed() * ServerGameAppState.getTimePerFrame();

        if (moveDistance > direction.length() - TEST_WEAPON_RANGE) {
            moveDistance = direction.length() - TEST_WEAPON_RANGE;
            //
                moveDistance = moveDistance < 0 ? 0 : moveDistance;
            //
        }

        moveVec = direction.normalizeLocal();
        moveVec.multLocal(moveDistance);

        Main.getInstance().enqueue(() -> {
            human.moveServer(hc.getServerContext(),moveVec);
        });

        if (human.getNode().getWorldTranslation().distance(target.getNode().getWorldTranslation()) <= TEST_WEAPON_RANGE) {
            return NodeCompletionStatus.SUCCESS;
        }

        return NodeCompletionStatus.FAILURE;
    }

}
