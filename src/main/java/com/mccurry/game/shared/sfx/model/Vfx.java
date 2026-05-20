package com.mccurry.game.shared.sfx.model;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class Vfx {
    protected final Consumer<Vfx> onFinishedPlaying;
    public Vfx(@Nullable Consumer<Vfx> onFinishedPlaying){
        this.onFinishedPlaying = onFinishedPlaying;
    }

    public abstract VfxType getType();
    public abstract void play(Node parentNode, Vector3f localOffset, Quaternion localRotation);
}
