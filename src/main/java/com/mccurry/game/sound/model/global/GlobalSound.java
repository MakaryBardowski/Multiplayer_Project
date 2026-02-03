package com.mccurry.game.sound.model.global;

import com.jme3.audio.AudioNode;
import com.mccurry.game.sound.model.Sound;
import com.mccurry.game.sound.model.SoundCategory;

public class GlobalSound extends Sound {

    public GlobalSound(AudioNode audioNode, SoundCategory soundCategory) {
        super(audioNode,soundCategory);
    }

    public void play(){
        audioNode.play(); // global sounds cannot "overlap"
    }
}
