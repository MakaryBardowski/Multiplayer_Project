package dirty.events;

import dirty.server.ServerContext;

public interface ServerEventSubscriber {
    void receiveEventNotificationServer(ServerContext ctx, GameEvent gameEvent);
}
