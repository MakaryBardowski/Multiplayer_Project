package com.mccurry.game.constants;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class CameraConstants {
    private static final Vector3f DEFAULT_CAMERA_POSITION = new Vector3f(0,0,0);
    private static final Vector3f DEFAULT_CAMERA_LOOK_AT_POS = new Vector3f(0,0,0);

    public static final void applyDefaultTransform(Camera camera){
        camera.setLocation(DEFAULT_CAMERA_POSITION.clone());
        camera.lookAt(DEFAULT_CAMERA_LOOK_AT_POS, Vector3f.UNIT_Y);
    }

}
