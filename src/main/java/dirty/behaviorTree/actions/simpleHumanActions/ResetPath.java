package dirty.behaviorTree.actions.simpleHumanActions;

import dirty.behaviorTree.NodeAction;
import dirty.behaviorTree.NodeCompletionStatus;
import static dirty.behaviorTree.NodeCompletionStatus.SUCCESS;
import dirty.behaviorTree.context.Context;
import dirty.behaviorTree.context.SimpleHumanMobContext;

public class ResetPath extends NodeAction{
    
        @Override
        public NodeCompletionStatus execute(Context context) {
            ((SimpleHumanMobContext) context).resetPath();
            return SUCCESS;
        }

}
