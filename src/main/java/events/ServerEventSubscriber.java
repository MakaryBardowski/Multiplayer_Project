package events;

import server.ServerContext;

public interface ServerEventSubscriber {
    void receiveEventNotificationServer(ServerContext ctx, GameEvent gameEvent);
}
