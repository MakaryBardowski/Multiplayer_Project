package dirty.events;

import dirty.server.ServerContext;

public interface ServerEventPublisher extends EventPublisher {
    void notifyEventSubscribersServer(ServerContext ctx, GameEvent gameEvent);
}
