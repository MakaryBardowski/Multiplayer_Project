package events;

interface EventPublisher {
     void addEventSubscriber(ServerEventSubscriber eventSubscriber);
     void removeEventSubscriber(ServerEventSubscriber eventSubscriber);
}
