package dirty.behaviorTree.actions.mudBeetleActions;

import dirty.behaviorTree.NodeAction;
import dirty.behaviorTree.NodeCompletionStatus;
import dirty.behaviorTree.context.Context;
import dirty.behaviorTree.context.MudBeetleContext;

import static dirty.behaviorTree.NodeCompletionStatus.SUCCESS;

public class GetCurrentTimestamp extends NodeAction{
    
        @Override
        public NodeCompletionStatus execute(Context context) {
            ((MudBeetleContext) context).setCurrentUpdateTimestamp(System.currentTimeMillis());
            return SUCCESS;
        }

}
