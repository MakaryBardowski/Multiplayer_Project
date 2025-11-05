package dirty.behaviorTree.composite;

import dirty.behaviorTree.context.Context;
import dirty.behaviorTree.BehaviorNode;
import dirty.behaviorTree.NodeCompletionStatus;

import java.util.List;

public class DecoratorNode extends CompositeNode{
    public DecoratorNode(List<BehaviorNode> children) {
        super(children);
    }

    @Override
    public NodeCompletionStatus execute(Context context) {
        return null;
    }
}
