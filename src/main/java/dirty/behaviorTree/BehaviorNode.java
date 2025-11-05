package dirty.behaviorTree;

import dirty.behaviorTree.context.Context;

public abstract class BehaviorNode {
    public abstract NodeCompletionStatus execute(Context context);
}
