package behaviorTree.context;

import subscriber.EventSubscriber;

import java.util.HashMap;
import java.util.Map;

public abstract class Context implements EventSubscriber {
    protected final Map<Integer,Object> blackboard = new HashMap<>();

    public Map<Integer, Object> getBlackboard() {
        return blackboard;
    }
    
    public abstract void shutdown();
    
}
