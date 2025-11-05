package dirty.behaviorTree.composite;

import dirty.behaviorTree.context.Context;
import dirty.behaviorTree.BehaviorNode;
import dirty.behaviorTree.NodeCompletionStatus;

import java.util.List;

public class SequenceNode extends CompositeNode {
    private String name;
    public SequenceNode(List<BehaviorNode> children) {
        super(children);
    }
    public SequenceNode(String name,List<BehaviorNode> children) {
        super(children);
        this.name = name;
    }
    @Override
    public NodeCompletionStatus execute(Context context) {

        for (int i = lastExecutedRunningChildIndex == -1 ? 0 : lastExecutedRunningChildIndex; i < children.size(); i++) {
            var child = children.get(i);
            var childStatus = child.execute(context);
            if (childStatus == NodeCompletionStatus.FAILURE || childStatus == NodeCompletionStatus.STOP_TREE_EXECUTION) {
                lastExecutedRunningChildIndex = -1;
                return childStatus;
            }
            if (childStatus == NodeCompletionStatus.RUNNING) {
                lastExecutedRunningChildIndex = i;
                return NodeCompletionStatus.RUNNING;
            }
        }
        lastExecutedRunningChildIndex = -1;
        return NodeCompletionStatus.SUCCESS;
    }

    @Override
    public String toString() {
        return "SequenceNode{" + "name=" + name + '}';
    }

   
}
