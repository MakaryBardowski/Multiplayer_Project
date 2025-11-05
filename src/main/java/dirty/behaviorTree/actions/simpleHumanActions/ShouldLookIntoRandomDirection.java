package dirty.behaviorTree.actions.simpleHumanActions;

import dirty.behaviorTree.NodeAction;
import dirty.behaviorTree.NodeCompletionStatus;
import static dirty.behaviorTree.NodeCompletionStatus.FAILURE;
import static dirty.behaviorTree.NodeCompletionStatus.SUCCESS;
import dirty.behaviorTree.context.Context;
import dirty.behaviorTree.context.SimpleHumanMobContext;

import java.util.Random;
import dirty.server.ServerGameAppState;

public class ShouldLookIntoRandomDirection extends NodeAction {

    private Random random = new Random();

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (SimpleHumanMobContext) context;
        hc.setLookInRandomDirectionTimer(hc.getLookInRandomDirectionTimer() - ServerGameAppState.getTimePerFrame());
        if (hc.getCurrentPath() == null && hc.getCurrentTarget() == null && hc.getLookInRandomDirectionTimer() <= 0) {
            return SUCCESS;
        }
        return FAILURE;
    }
}
