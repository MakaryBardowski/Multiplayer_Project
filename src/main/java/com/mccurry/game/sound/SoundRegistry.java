package com.mccurry.game.sound;

import com.jme3.asset.AssetManager;

import com.jme3.audio.AudioNode;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.sound.asset.GlobalSoundAsset;
import com.mccurry.game.sound.asset.PositionalSoundAsset;
import com.mccurry.game.sound.event.MainThreadSoundVolumeChangedEvent;
import com.mccurry.game.sound.model.Sound;
import com.mccurry.game.sound.model.SoundCategory;
import com.mccurry.game.sound.model.global.GlobalSoundEntry;
import com.mccurry.game.sound.model.global.GlobalSound;
import com.mccurry.game.sound.model.positional.PositionalSound;
import com.mccurry.game.sound.asset.SoundAssetRegistry;
import com.mccurry.game.sound.model.positional.PositionalSoundEntry;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundRegistry implements SyncEventHandler<MainThreadSoundVolumeChangedEvent>, EventSubscriber {
    private final Map<PositionalSoundEntry, PositionalSound> POSITIONAL_SOUNDS = new HashMap<>();
    private final Map<GlobalSoundEntry, GlobalSound> GLOBAL_SOUNDS = new HashMap<>();

    private final List<Map<? ,? extends Sound>> ALL_SOUND_MAPS = new ArrayList<>(){{
        add(POSITIONAL_SOUNDS);
        add(GLOBAL_SOUNDS);
    }};

    public static SoundRegistry withSoundVolumesFromSettings(AssetManager assetManager, UserSettings userSettings){
        return new SoundRegistry(assetManager,userSettings);
    }

    private SoundRegistry(AssetManager assetManager, UserSettings settings){
        initialize(assetManager, settings);
    }

    private void initialize(AssetManager assetManager, UserSettings settings){
        for(var globalSoundAsset : SoundAssetRegistry.GLOBAL_SOUND_ASSETS){
            GLOBAL_SOUNDS.put(
                    globalSoundAsset.getSoundEntry(),
                    instantiateGlobalSound(globalSoundAsset,assetManager)
            );
        }

        for(var positionalSoundAsset : SoundAssetRegistry.POSITIONAL_SOUND_ASSETS){
            POSITIONAL_SOUNDS.put(
                    positionalSoundAsset.getSoundEntry(),
                    instantiatePositionalSound(positionalSoundAsset,assetManager)
            );
        }

        var initialSoundVolumes = settings.getSoundSettings();

        updateSoundVolumes(
                MainThreadSoundVolumeChangedEvent.builder()
                        .masterVolumePercentage(initialSoundVolumes.getMasterVolume())
                        .musicVolumePercentage(initialSoundVolumes.getMusicVolume())
                        .sfxVolumePercentage(initialSoundVolumes.getSfxVolume())
                        .gunshotVolumePercentage(initialSoundVolumes.getGunshotVolume())
                        .build()
        );
    }


    private static PositionalSound instantiatePositionalSound(PositionalSoundAsset soundAssetData, AssetManager assetManager){
        var audioNode = new AudioNode(assetManager, soundAssetData.getAssetPath(), soundAssetData.getType());
        audioNode.setPositional(true);
        audioNode.setLooping(false);


        return new PositionalSound(audioNode, soundAssetData.getSoundCategory());
    }


    private static GlobalSound instantiateGlobalSound(GlobalSoundAsset soundAssetData, AssetManager assetManager){
        var audioNode = new AudioNode(assetManager, soundAssetData.getAssetPath(), soundAssetData.getType());
        audioNode.setPositional(false);
        audioNode.setLooping(false);
        
        return new GlobalSound(audioNode, soundAssetData.getSoundCategory());
    }

    public PositionalSound getPositionalSound(PositionalSoundEntry soundEntry){
        return POSITIONAL_SOUNDS.get(soundEntry);
    }

    public GlobalSound getGlobalSound(GlobalSoundEntry soundEntry){
        return GLOBAL_SOUNDS.get(soundEntry);
    }

    @Override
    public void handle(@NonNull MainThreadSoundVolumeChangedEvent event, @Nullable EventLifecycleManager<MainThreadSoundVolumeChangedEvent> lifecycleManager) {
        updateSoundVolumes(event);
    }

    private int getVolumeForSoundCategory(SoundCategory soundCategory, MainThreadSoundVolumeChangedEvent event){
        if(SoundCategory.MUSIC.equals(soundCategory)){
            return event.getMusicVolumePercentage();
        } else if(SoundCategory.SFX.equals(soundCategory)){
            return event.getSfxVolumePercentage();
        } else if (SoundCategory.GUNSHOT.equals(soundCategory)){
            return event.getGunshotVolumePercentage();
        }
        return 0;
    }

    private void updateSoundVolumes(MainThreadSoundVolumeChangedEvent event){
        var masterVolume = ( (float)event.getMasterVolumePercentage() ) / 100;

        for(var sounds : ALL_SOUND_MAPS) {
            for (var sound : sounds.values()) {
                var soundCategory = sound.getSoundCategory();

                var volumeForCategory = ( (float)getVolumeForSoundCategory(soundCategory, event) ) / 100;
                sound.setVolumeMultiplier(
                        volumeForCategory * masterVolume
                );
            }
        }
    }

    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(MainThreadSoundVolumeChangedEvent.class, this);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(MainThreadSoundVolumeChangedEvent.class, this);
    }
}
