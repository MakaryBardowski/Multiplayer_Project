package dirty.behaviorTree.actions.mudBeetleActions;

import dirty.behaviorTree.NodeAction;
import dirty.behaviorTree.NodeCompletionStatus;
import dirty.behaviorTree.context.Context;
import dirty.behaviorTree.context.MudBeetleContext;

import static dirty.behaviorTree.NodeCompletionStatus.SUCCESS;

public class ResetPath extends NodeAction{
    
        @Override
        public NodeCompletionStatus execute(Context context) {
            ((MudBeetleContext) context).resetPath();
            return SUCCESS;
        }

}
