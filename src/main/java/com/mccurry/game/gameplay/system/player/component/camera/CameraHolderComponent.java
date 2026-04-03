package com.mccurry.game.gameplay.system.player.component.camera;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.core.ecs.component.SingletonComponent;
import lombok.Getter;

@Getter
public class CameraHolderComponent implements SingletonComponent {
    private final Vector3f currentCameraOffset;
    private final Vector3f cameraRestOffset;

    private final Quaternion currentCameraRotation;

    public CameraHolderComponent(Vector3f cameraRestOffset){
        this.cameraRestOffset = cameraRestOffset.clone();
        this.currentCameraOffset = this.cameraRestOffset.clone();
        this.currentCameraRotation = Quaternion.IDENTITY.clone();
    }
}
