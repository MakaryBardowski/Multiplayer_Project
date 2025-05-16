package game.entities;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import events.GameEvent;
import lombok.Getter;
import lombok.Setter;
import messages.*;
import publisher.EventPublisher;
import server.ServerContext;
import server.ServerGameAppState;
import subscriber.EventSubscriber;

@Getter
public abstract class Entity implements EventPublisher, EventSubscriber {
    private final ArrayList<EventSubscriber> subscribers = new ArrayList<>(1);

    private static final DeleteEntityMessage dem = new DeleteEntityMessage();

    @Getter
    @Setter
    private BiConsumer<GameEvent,Entity> gameEventHandler;

    @Getter
    protected ConcurrentHashMap<Integer, Attribute> attributes = new ConcurrentHashMap<>(0);

    protected int id;
    @Setter
    protected String name;

    public Entity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public final void destroyOnServerAndNotify(ServerContext context) {
        destroyServer();
        dem.setId(id);
        context.notifyAllClients(dem);
    }
    // this should be abstract!
    public void destroyClient(){};
    // this should be abstract!

    public void destroyServer(){};

    public abstract TwoWayMessage createNewEntityMessage();

    public void attributeChangedNotification(int attributeId, Attribute oldAttributeCopy, Attribute copyOfNewAttribute) {}

    public void setFloatAttributeAndNotifyClients(int attributeId, float val) {
        setFloatAttribute(attributeId, val);
        var msg = new EntitySetFloatAttributeMessage(this, attributeId, val);
        ServerGameAppState.getInstance().getServer().broadcast(msg);
    }

    public void setIntegerAttributeAndNotifyClients(int attributeId, int val) {
        setIntegerAttribute(attributeId, val);
        var msg = new EntitySetIntegerAttributeMessage(this, attributeId, val);
        ServerGameAppState.getInstance().getServer().broadcast(msg);
    }

    public void setFloatAttributesAndNotifyClients(Map<Integer,Float> floatAttributesById) {
        setFloatAttributes(floatAttributesById);
        var msg = new BatchSetFloatAttributeMessage(this, floatAttributesById);
        ServerGameAppState.getInstance().getServer().broadcast(msg);
    }

    public void setIntegerAttributesAndNotifyClients(Map<Integer,Integer> integerAttributesById) {
        setIntegerAttributes(integerAttributesById);
        var msg = new BatchSetIntegerAttributeMessage(this, integerAttributesById);
        ServerGameAppState.getInstance().getServer().broadcast(msg);
    }

    public void setFloatAttribute(int attributeId, float val) {
        var oldValue = getFloatAttribute(attributeId).getValue();
        getFloatAttribute(attributeId).setValue(val);
        attributeChangedNotification(attributeId,new FloatAttribute(oldValue),new FloatAttribute(val));
    }

    public void setIntegerAttribute(int attributeId, int val) {
        var oldValue = getIntegerAttribute(attributeId).getValue();
        getIntegerAttribute(attributeId).setValue(val);
        attributeChangedNotification(attributeId, new IntegerAttribute(oldValue),new IntegerAttribute(val));
    }

    public void setFloatAttributes(Map<Integer,Float> integerAttributesById) {
        for(var entry : integerAttributesById.entrySet() ){
            setFloatAttribute(entry.getKey(), entry.getValue());
        }
    }

    public void setIntegerAttributes(Map<Integer,Integer> integerAttributesById) {
        for(var entry : integerAttributesById.entrySet() ){
            setIntegerAttribute(entry.getKey(), entry.getValue());
        }
    }

    public FloatAttribute getFloatAttribute(int attributeId) {
        return ((FloatAttribute) attributes.get(attributeId));
    }

    public IntegerAttribute getIntegerAttribute(int attributeId) {
        return ((IntegerAttribute) attributes.get(attributeId));
    }

    @Override
    public int hashCode() {
        return id;
    }

    public final void addEventSubscriber(EventSubscriber eventSubscriber){
        subscribers.add(eventSubscriber);
    }

    public void removeEventSubscriber(EventSubscriber eventSubscriber){
        subscribers.remove(eventSubscriber);
    }

    public void notifyEventSubscribers(GameEvent gameEvent){
        for(var subscriber : subscribers){
            subscriber.receiveEventNotification(gameEvent);
        }
    }

    @Override
    public void receiveEventNotification(GameEvent gameEvent) {
        if(gameEventHandler != null){
            gameEventHandler.accept(gameEvent,this);
        }
    }
}
