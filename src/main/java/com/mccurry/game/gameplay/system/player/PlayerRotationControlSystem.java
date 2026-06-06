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

@RequiredArgsConstructor
public class PlayerRotationControlSystem implements AnalogListener {
    private final EntityStore entityStore;

    private float yaw = 0;
    private float pitch = 0;

    private final float mouseSensitivity = 90f;
    private final float maxPitch = 89f;
    private final float minPitch = -89f;

    @Override
    public void onAnalog(String name, float value, float tpf) {
        var playerEntity = entityStore.getSingletonComponentHolder(PlayerControlledComponent.class);
        if (playerEntity == null) {
            return;
        }

        if (name.equals(KeyMappingRegistry.MAPPING_MOUSE_MOVE_X_LEFT)) {
            yaw += value * mouseSensitivity;
        } else if (name.equals(KeyMappingRegistry.MAPPING_MOUSE_MOVE_X_RIGHT)) {
            yaw -= value * mouseSensitivity;
        } else if (name.equals(KeyMappingRegistry.MAPPING_MOUSE_MOVE_Y_UP)) {
            pitch += value * mouseSensitivity;
            pitch = Math.max(minPitch, Math.min(maxPitch, pitch));
        } else if (name.equals(KeyMappingRegistry.MAPPING_MOUSE_MOVE_Y_DOWN)) {
            pitch -= value * mouseSensitivity;
            pitch = Math.max(minPitch, Math.min(maxPitch, pitch));
        }

        // Apply rotation to the player's visual node
        var transform = entityStore.getComponent(playerEntity, TransformComponent.class);
        if (transform == null) {
            return;
        }

        var rotation = new Quaternion();
        rotation.fromAngles(
                0,
                FastMath.DEG_TO_RAD * yaw,
                0);

        transform.getVisualNode().setLocalRotation(rotation);

        var camera = entityStore.getComponent(playerEntity, CameraHolderComponent.class);
        if (camera == null) {
            return;
        }

        camera.getCurrentDesiredCameraRotation().fromAngles(
                FastMath.DEG_TO_RAD * pitch,
                FastMath.DEG_TO_RAD * yaw,
                0);
    }
}
