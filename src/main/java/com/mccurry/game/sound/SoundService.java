package com.mccurry.game.sound;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.EventHandler;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import com.mccurry.game.util.event.EventHandlers;
import com.mccurry.game.sound.event.PlayGlobalSoundRequestEvent;
import com.mccurry.game.sound.event.PlayPositionalSoundRequestEvent;

import lombok.NonNull;

public class SoundService implements EventSubscriber<GameOrchestrationContext> {
//    private Application application;
//    private AssetManager assetManager;
//    private Thread mainJmeThread;
//    private Node rootSoundNode;
//    private SoundRegistry soundRegistry;

    private EventHandler<PlayPositionalSoundRequestEvent, GameOrchestrationContext> onPositionalSound;
    private EventHandler<PlayGlobalSoundRequestEvent, GameOrchestrationContext> onGlobalSound;

    public SoundService(
            Thread mainJmeThread, Application application,
            Node rootSoundNode,
            SoundRegistry soundRegistry
    ){
//        this.mainJmeThread = mainJmeThread;
//        this.application = application;
//        this.assetManager = application.getAssetManager();
//        this.rootSoundNode = rootSoundNode;
//        this.soundRegistry = soundRegistry;

        this.onPositionalSound = EventHandlers.syncEventHandler((event, ctx, manager)->{
            var soundToPlay = soundRegistry.getPositionalSound(event.getSoundEntry());
            var x = event.getX();
            var y = event.getY();
            var z = event.getZ();
            // unpacking event so when its reused before enqueud plays its all ok
            if(Thread.currentThread() != mainJmeThread){
                application.enqueue(()->{
                    soundToPlay.playAt(x,y,z);
                });
                return;
            }
            soundToPlay.playAt(x,y,z);
        });

        this.onGlobalSound = EventHandlers.syncEventHandler((event, ctx, manager)->{
            var soundToPlay = soundRegistry.getGlobalSound(event.getSoundEntry());
            if(Thread.currentThread() != mainJmeThread){
                application.enqueue(()->{
                    soundToPlay.play();
                });
                return;
            }
            soundToPlay.play();
        });
    }

    @Override
    public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(PlayPositionalSoundRequestEvent.class, onPositionalSound);
        eventBus.subscribe(PlayGlobalSoundRequestEvent.class, onGlobalSound);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(PlayPositionalSoundRequestEvent.class, onPositionalSound);
        eventBus.unsubscribe(PlayGlobalSoundRequestEvent.class, onGlobalSound);
    }
}
