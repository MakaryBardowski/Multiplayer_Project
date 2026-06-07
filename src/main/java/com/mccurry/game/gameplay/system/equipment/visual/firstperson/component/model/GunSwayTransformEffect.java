package com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.model;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.system.player.component.camera.model.TransformEffect;

import static com.mccurry.game.gameplay.sharedcomponents.transformeffect.TransformEffectConstants.FIXED_FRAMERATE;

public class GunSwayTransformEffect extends TransformEffect {

    public static final float SWAY_SPEED = 1f / FIXED_FRAMERATE;

    private final float frequency = 7f;

    private final float amplitudeX = 0.02f; // left-right
    private final float amplitudeY = 0.015f; // slight up at center

    private final float wavelength = 2 * FastMath.PI;

    private float time = 0f;

    private final float fadeSpeed = 10f;

    private void updateWeight(float tpf) {
        weight += (targetWeight - weight) * (1f - FastMath.exp(-tpf * fadeSpeed));
    }

    @Override
    public void applyPosition(Vector3f cameraOffset) {
        float tpf = SWAY_SPEED;

        updateWeight(tpf);

        if (weight < 0.0001f && targetWeight == 0f) {
            cleanup = true;
            return;
        }

        float cycleLength = wavelength / frequency;

        time += SWAY_SPEED;

        if (time >= cycleLength) {
            time -= cycleLength;
        }

        float s = (float) Math.sin(time * frequency);

        // Left-right sway
        float swayX = s * amplitudeX;

        // Peak upward at center (when sin ≈ 0)
        // Using (1 - |sin|) gives a bump in the middle
        float centerLift = 1f - Math.abs(s);
        float swayY = centerLift * amplitudeY;

        cameraOffset.addLocal(
                swayX * weight,
                swayY * weight,
                0f
        );
    }

    @Override
    public void applyRotation(Quaternion cameraRotation) {

    }
}