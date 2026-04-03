package com.mccurry.game.gameplay.system.player.component.camera.model;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.jetbrains.annotations.Nullable;


public interface CameraEffect {
    void applyPosition(Vector3f cameraOffset);
    void applyRotation(Quaternion cameraRotation);


    boolean isFinished();
}
