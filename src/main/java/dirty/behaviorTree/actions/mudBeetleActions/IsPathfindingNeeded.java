package dirty.behaviorTree.actions.mudBeetleActions;

import dirty.behaviorTree.NodeAction;
import dirty.behaviorTree.NodeCompletionStatus;
import dirty.behaviorTree.context.Context;
import dirty.behaviorTree.context.MudBeetleContext;
import dirty.server.ServerGameAppState;

public class IsPathfindingNeeded extends NodeAction {

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (MudBeetleContext) context;
        hc.setFindRandomPathTimer(hc.getFindRandomPathTimer() - ServerGameAppState.getTimePerFrame());
        if (shouldPathfind(hc)) {
            hc.setLookInRandomDirectionTimer(hc.getLookInRandomDirectionCooldown());
            hc.setFindRandomPathTimer(hc.getFindRandomPathCooldown());
            return NodeCompletionStatus.SUCCESS;
        }
        return NodeCompletionStatus.FAILURE;
    }

    private boolean shouldPathfind(MudBeetleContext hc) {
        return (hc.getCurrentPath() == null
                && hc.getFindRandomPathTimer() < 0
                && hc.getCurrentTarget() == null)
                || hc.getLastKnownTargetPosition() != null /*
                always pathfind.
                 */;
    }
}
