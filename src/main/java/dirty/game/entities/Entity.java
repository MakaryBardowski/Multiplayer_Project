package dirty.game.entities;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dirty.Utils.TriConsumer;

import dirty.events.ServerEventSubscriber;
import dirty.events.GameEvent;
import dirty.events.ServerEventPublisher;
import dirty.messages.*;
import lombok.Getter;
import lombok.Setter;
import dirty.server.ServerContext;


@Getter
public abstract class Entity implements ServerEventPublisher, ServerEventSubscriber {
    public static final int NOT_DETERMINED_ID = -1;
    private final ArrayList<ServerEventSubscriber> subscribers = new ArrayList<>(1);

    private static final DeleteEntityMessage dem = new DeleteEntityMessage();

    @Getter
    @Setter
    private TriConsumer<ServerContext,GameEvent,Entity> serverGameEventHandler;

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
        destroyServer(context);
        dem.setId(id);
        context.notifyAllClients(dem);
    }
    // this should be abstract!
    public void destroyClient(){};
    // this should be abstract!

    public void destroyServer(ServerContext ctx){};

    public abstract TwoWayMessage createNewEntityMessage();

    public void attributeChangedNotification(int attributeId, Attribute oldAttributeCopy, Attribute copyOfNewAttribute) {}

    public void setFloatAttributeAndNotifyClients(ServerContext ctx, int attributeId, float val) {
        setFloatAttribute(attributeId, val);
        var msg = new EntitySetFloatAttributeMessage(this, attributeId, val);
        ctx.notifyAllClients(msg);
    }

    public void setIntegerAttributeAndNotifyClients(ServerContext ctx, int attributeId, int val) {
        setIntegerAttribute(attributeId, val);
        var msg = new EntitySetIntegerAttributeMessage(this, attributeId, val);
        ctx.notifyAllClients(msg);
    }

    public void setFloatAttributesAndNotifyClients(ServerContext ctx, Map<Integer,Float> floatAttributesById) {
        setFloatAttributes(floatAttributesById);
        var msg = new BatchSetFloatAttributeMessage(this, floatAttributesById);
        ctx.notifyAllClients(msg);
    }

    public void setIntegerAttributesAndNotifyClients(ServerContext ctx, Map<Integer,Integer> integerAttributesById) {
        setIntegerAttributes(integerAttributesById);
        var msg = new BatchSetIntegerAttributeMessage(this, integerAttributesById);
        ctx.notifyAllClients(msg);
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

    @Override
    public final void addEventSubscriber(ServerEventSubscriber eventSubscriber){
        subscribers.add(eventSubscriber);
    }

    @Override
    public void removeEventSubscriber(ServerEventSubscriber eventSubscriber){
        subscribers.remove(eventSubscriber);
    }

    @Override
    public void notifyEventSubscribersServer(ServerContext ctx, GameEvent gameEvent){
        for(var subscriber : subscribers){
            subscriber.receiveEventNotificationServer(ctx,gameEvent);
        }
    }

    @Override
    public void receiveEventNotificationServer(ServerContext ctx, GameEvent gameEvent) {
        if(serverGameEventHandler != null){
            serverGameEventHandler.accept(ctx,gameEvent,this);
        }
    }
}
