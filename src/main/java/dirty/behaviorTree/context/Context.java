package dirty.behaviorTree.context;

import dirty.events.ServerEventSubscriber;

import java.util.HashMap;
import java.util.Map;

public abstract class Context implements ServerEventSubscriber {
    protected final Map<Integer,Object> blackboard = new HashMap<>();

    public Map<Integer, Object> getBlackboard() {
        return blackboard;
    }
    
    public abstract void shutdown();
    
}
