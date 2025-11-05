package dirty.behaviorTree;

import dirty.behaviorTree.context.Context;

public abstract class NodeAction {
   public abstract NodeCompletionStatus execute(Context context);
}
