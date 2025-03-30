package events;

import java.util.ArrayList;

public interface EventPublisher {
     void addEventSubscriber(EventSubscriber eventSubscriber);
     void removeEventSubscriber(EventSubscriber eventSubscriber);
     void notifyEventSubscribers(GameEvent gameEvent);
    
}
