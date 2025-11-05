package dirty.behaviorTree;

import dirty.behaviorTree.context.Context;
import lombok.Getter;

public class BehaviorTree {
    @Getter
    private Context context;
    private BehaviorNode rootNode;

    public BehaviorTree(BehaviorNode rootNode, Context context) {
        this.context = context;
        this.rootNode = rootNode;
    }

    public void update() {
        rootNode.execute(context);
    }
    
    public void shutdown(){
        context.shutdown();
    };

}
