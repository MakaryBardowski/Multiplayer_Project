package dirty.behaviorTree.actions.mudBeetleActions;

import dirty.behaviorTree.NodeAction;
import dirty.behaviorTree.NodeCompletionStatus;
import dirty.behaviorTree.context.Context;
import dirty.behaviorTree.context.MudBeetleContext;
import dirty.server.ServerGameAppState;

import java.util.Random;

import static dirty.behaviorTree.NodeCompletionStatus.FAILURE;
import static dirty.behaviorTree.NodeCompletionStatus.SUCCESS;

public class ShouldLookIntoRandomDirection extends NodeAction {

    private Random random = new Random();

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (MudBeetleContext) context;
        hc.setLookInRandomDirectionTimer(hc.getLookInRandomDirectionTimer() - ServerGameAppState.getTimePerFrame());
        if (hc.getCurrentPath() == null && hc.getCurrentTarget() == null && hc.getLookInRandomDirectionTimer() <= 0) {
            return SUCCESS;
        }
        return FAILURE;
    }
}
