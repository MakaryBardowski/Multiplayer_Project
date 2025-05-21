package events;

import server.ServerContext;

public interface ServerEventPublisher extends EventPublisher {
    void notifyEventSubscribersServer(ServerContext ctx, GameEvent gameEvent);
}
