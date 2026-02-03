package com.mccurry.game.sound.model.positional;


import com.jme3.audio.AudioNode;
import com.mccurry.game.sound.model.Sound;
import com.mccurry.game.sound.model.SoundCategory;

public class PositionalSound extends Sound {

    public PositionalSound(AudioNode audioNode, SoundCategory soundCategory) {
        super(audioNode,soundCategory);
    }

    public void playAt(int x, int y, int z){
        audioNode.setLocalTranslation(x,y,z);
        audioNode.playInstance();
    };
}
