package com.mccurry.game.gameplay.system.player.component.camera.model.movement.headbob;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.system.player.component.camera.model.TransformEffect;

import static com.mccurry.game.gameplay.sharedcomponents.transformeffect.TransformEffectConstants.FIXED_FRAMERATE;

public class HeadbobTransformEffect extends TransformEffect {

    public static final float HEADBOB_SPEED = 1f / FIXED_FRAMERATE;

    private final float frequency = 15f;
    private final float amplitude = 0.06f;
    private final float wavelength = 2* FastMath.PI;

    private float time = 0f;

    private final float fadeSpeed = 10f;


    private void updateWeight(float tpf) {
        weight += (targetWeight - weight) * (1f - FastMath.exp(-tpf * fadeSpeed));
    }

    @Override
    public void applyPosition(Vector3f cameraOffset) {
        float tpf = HEADBOB_SPEED;

        updateWeight(tpf);

        if (weight < 0.0001f && targetWeight == 0f) {
            cleanup = true;
            return;
        }

        float cycleLength = wavelength / frequency;

        time += HEADBOB_SPEED;

        // wrap safely
        if (time >= cycleLength) {
            time -= cycleLength;

            // onStep()
        }

        float s = (float) Math.sin(time * frequency);

        // asymmetric bob
        float shaped = (s >= 0f)
                ? s * 0.6f
                : s * 1.4f;

        float offsetFromHeadbob = shaped * amplitude;

        cameraOffset.addLocal(0f, offsetFromHeadbob * weight, 0f);
    }

    @Override
    public void applyRotation(Quaternion cameraRotation) {
        // no rotation for headbob
    }
}