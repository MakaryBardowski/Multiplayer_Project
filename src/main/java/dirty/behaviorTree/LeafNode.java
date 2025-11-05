package dirty.behaviorTree;

import dirty.behaviorTree.context.Context;

public class LeafNode extends BehaviorNode{
    private NodeAction nodeAction;
    public LeafNode(NodeAction nodeAction){
    this.nodeAction = nodeAction;
    }

    @Override
    public NodeCompletionStatus execute(Context context) {
        return nodeAction.execute(context);
    }

    public NodeAction getNodeAction() {
        return nodeAction;
    }

    @Override
    public String toString() {
        return "LeafNode<" + "nodeAction=" + nodeAction + '>';
    }
    
    
}
