package com.mccurry.game.sound.model;

import com.jme3.audio.AudioNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Sound {
    protected final int baseVolume = 1;
    protected final AudioNode audioNode;
    @Getter
    protected final SoundCategory soundCategory;

    public void setVolumeMultiplier(float volumeMultiplier){
        audioNode.setVolume(volumeMultiplier*baseVolume);
    }
}
