package com.mccurry.game.sound;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.EventHandler;
import com.mccurry.game.threading.ThreadingTools;
import com.mccurry.game.util.event.EventHandlers;
import com.mccurry.game.sound.event.PlayGlobalSoundRequestEvent;
import com.mccurry.game.sound.event.PlayPositionalSoundRequestEvent;

import lombok.NonNull;

public class SoundService implements EventSubscriber {
//    private Application application;
//    private AssetManager assetManager;
//    private Node rootSoundNode;
//    private SoundRegistry soundRegistry;

    private EventHandler<PlayPositionalSoundRequestEvent> onPositionalSound;
    private EventHandler<PlayGlobalSoundRequestEvent> onGlobalSound;

    public SoundService(
            Application application,
            Node rootSoundNode,
            SoundRegistry soundRegistry
    ){
//        this.mainJmeThread = mainJmeThread;
//        this.application = application;
//        this.assetManager = application.getAssetManager();
//        this.rootSoundNode = rootSoundNode;
//        this.soundRegistry = soundRegistry;

        this.onPositionalSound = EventHandlers.syncEventHandler((event, manager)->{
            var soundToPlay = soundRegistry.getPositionalSound(event.getSoundEntry());
            var x = event.getX();
            var y = event.getY();
            var z = event.getZ();
            // unpacking event so when its reused before enqueud plays its all ok
            ThreadingTools.smartEnqueue(()->{
                soundToPlay.playAt(x,y,z);
            });
        });

        this.onGlobalSound = EventHandlers.syncEventHandler((event, manager)->{
            var soundToPlay = soundRegistry.getGlobalSound(event.getSoundEntry());
            ThreadingTools.smartEnqueue(()->{
                soundToPlay.play();
            });
        });
    }

    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(PlayPositionalSoundRequestEvent.class, onPositionalSound);
        eventBus.subscribe(PlayGlobalSoundRequestEvent.class, onGlobalSound);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(PlayPositionalSoundRequestEvent.class, onPositionalSound);
        eventBus.unsubscribe(PlayGlobalSoundRequestEvent.class, onGlobalSound);
    }
}
