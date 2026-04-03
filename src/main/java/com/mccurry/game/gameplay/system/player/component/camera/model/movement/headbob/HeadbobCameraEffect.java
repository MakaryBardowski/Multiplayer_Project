package com.mccurry.game.gameplay.system.player.component.camera.model.movement.headbob;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.system.player.component.camera.model.CameraEffect;
import lombok.Getter;

import static com.mccurry.game.gameplay.system.player.component.camera.CameraEffectConstants.FIXED_FRAMERATE;

public class HeadbobCameraEffect implements CameraEffect {
    public static final float HEADBOB_SPEED = 1f/FIXED_FRAMERATE;

    private float headBobTimer=0;
    private final float frequency = 15;
    private final float amplitude = 0.018f*1.25f;
    private final float wavelength = 2* FastMath.PI;

    @Getter
    private float totalOffsetFromHeadbob = 0;


    @Override
    public void applyPosition(Vector3f cameraOffset) {
            int nextStep = (int) (1 + Math.floor((headBobTimer)*frequency/wavelength));
            float nextStepTime = (nextStep*wavelength)/frequency;
            headBobTimer = Math.min(headBobTimer+HEADBOB_SPEED, nextStepTime);
            var offsetFromHeadbob = (float) (Math.sin(headBobTimer*frequency)*amplitude);
            totalOffsetFromHeadbob += offsetFromHeadbob;

            cameraOffset.addLocal(0, offsetFromHeadbob, 0);
    }

    @Override
    public void applyRotation(Quaternion cameraRotation) {}

    @Override
    public boolean isFinished() {
        return false;
    }
}