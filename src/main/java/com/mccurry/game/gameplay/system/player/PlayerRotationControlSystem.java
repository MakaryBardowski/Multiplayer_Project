package com.mccurry.game.gameplay.system.player;

import com.jme3.input.controls.AnalogListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.player.component.control.PlayerControlledComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import com.mccurry.game.keyBinding.KeyMappingRegistry;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public class PlayerRotationControlSystem implements AnalogListener {
    private final EntityStore entityStore;

    private final float mouseSensitivity = 90f;
    private final float maxPitch = 89f;
    private final float minPitch = -89f;

    private final float[] tempAngles = new float[3];

    @Override
    public void onAnalog(String name, float value, float tpf) {
        var playerEntity = entityStore.getSingletonComponentHolder(PlayerControlledComponent.class);
        if (playerEntity == null) {
            return;
        }

        // Apply rotation to the player's visual node
        var transform = entityStore.getComponent(playerEntity, TransformComponent.class);
        if (transform == null) {
            return;
        }

        var camera = entityStore.getComponent(playerEntity, CameraHolderComponent.class);
        if (camera == null) {
            return;
        }


        camera.getCurrentDesiredCameraRotation().toAngles(tempAngles);
        var currentPitch = FastMath.RAD_TO_DEG * tempAngles[0];
        var currentYaw = FastMath.RAD_TO_DEG * tempAngles[1];




        float yawDelta = 0;
        float pitchDelta = 0;
        if (name.equals(KeyMappingRegistry.MAPPING_MOUSE_MOVE_X_LEFT)) {
            yawDelta += value * mouseSensitivity;
        } else if (name.equals(KeyMappingRegistry.MAPPING_MOUSE_MOVE_X_RIGHT)) {
            yawDelta -= value * mouseSensitivity;
        } else if (name.equals(KeyMappingRegistry.MAPPING_MOUSE_MOVE_Y_UP)) {
            pitchDelta += value * mouseSensitivity;
        } else if (name.equals(KeyMappingRegistry.MAPPING_MOUSE_MOVE_Y_DOWN)) {
            pitchDelta -= value * mouseSensitivity;
        }

        var pitch = currentPitch + pitchDelta;
        pitch = Math.clamp(pitch, minPitch, maxPitch);

        var yaw = currentYaw + yawDelta;

        var rotation = new Quaternion();
        rotation.fromAngles(
                0,
                FastMath.DEG_TO_RAD * yaw,
                0);

        transform.getVisualNode().setLocalRotation(rotation);


        camera.getCurrentDesiredCameraRotation().fromAngles(
                FastMath.DEG_TO_RAD * pitch,
                FastMath.DEG_TO_RAD * yaw,
                0);

    }
}
