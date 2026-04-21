package com.mccurry.game.gameplay.system.player.component.camera.model;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import lombok.AccessLevel;
import lombok.Getter;


public abstract class CameraEffect {
    protected float weight = 0f;
    protected float targetWeight = 0f;
    @Getter(AccessLevel.PUBLIC)
    protected boolean cleanup;

    public abstract void applyPosition(Vector3f cameraOffset);
    public abstract void applyRotation(Quaternion cameraRotation);

    public final void setEnabled(boolean enabled) {
        targetWeight = enabled ? 1f : 0f;
    }
}
