package com.mccurry.game.gameplay.system.player.component.camera.model.movement.headbob;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.system.player.component.camera.model.CameraEffect;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class HeadbobToIdleCameraEffect implements CameraEffect {
    private final float offsetFromHeadbob;
    private float alreadyRecoveredY = 0;

    @Override
    public void applyPosition(Vector3f cameraOffset) {
        float speed = 0.1f;
        float direction = Math.signum(offsetFromHeadbob);

        float remaining = Math.abs(offsetFromHeadbob) - Math.abs(alreadyRecoveredY);
        float compensation = Math.min(speed, remaining);

        cameraOffset.addLocal(0, -direction * compensation, 0);
        alreadyRecoveredY += compensation;
    }

    @Override
    public void applyRotation(Quaternion cameraRotation) {}

    @Override
    public boolean isFinished() {
        return alreadyRecoveredY >= Math.abs(offsetFromHeadbob);
    }
}