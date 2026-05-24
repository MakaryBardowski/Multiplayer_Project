package pocs;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.system.player.component.camera.model.TransformEffect;
import com.mccurry.game.shared.noise.PerlinNoise;

import java.util.Random;

import static com.mccurry.game.gameplay.sharedcomponents.transformeffect.TransformEffectConstants.FIXED_FRAMERATE;

public class SmgCameraRecoilTransformEffect extends TransformEffect {
    private static final Random RANDOM = new Random();

    //    private final float sideKick = 0.0045f;

    // rotation recoil tuning (independent)
    private final float rotUpMagnitude = 0.0055f;
    private final float rotSideMagnitude = 0.007f;

    // control
    private final float recoverySpeed = 11f;
    private final float damping = 16f;

    // POSITION recoil
    private float recoilX = 0f;
    private float recoilY = 0f;
    private float recoilZ = 0f;

    private float velY = 0f;
    private float velZ = 0f;

    // ROTATION recoil (independent system)
    private float rotPitch = 0f;
    private float rotYaw = 0f;

    private float rotVelPitch = 0f;
    private float rotVelYaw = 0f;

    private static float perlinX = 0;
    private static float perlinY = 0;

    public void onShot() {
        // POSITION recoil

        perlinX+=0.2f;
        perlinY+=0.2f;

        // no trnaslation so i dont get motion sick
//        float upKick = 0.0055f;
//        velY += RANDOM.nextFloat(upKick * 0.8f, upKick);
//        velZ += RANDOM.nextFloat(0.05f, 0.07f);

        // ROTATION recoil (completely independent)
        rotVelPitch += RANDOM.nextFloat(rotUpMagnitude * 0.8f, rotUpMagnitude);
        rotVelYaw   += (float) PerlinNoise.noise(perlinX,perlinY) * rotSideMagnitude;

        targetWeight = 1f;
    }

    @Override
    public void applyPosition(Vector3f cameraOffset) {
        float tpf = 1f / FIXED_FRAMERATE;

        weight = targetWeight;

        // integrate position recoil
        recoilY += velY;
        recoilZ += -velZ;

        // soft cap vertical
        float maxUp = 0.12f;
        if (recoilY > maxUp) {
            recoilY = FastMath.interpolateLinear(0.2f, recoilY, maxUp);
        }

        // damping
        velY *= (1f - tpf * damping);
        velZ *= (1f - tpf * damping);

        // recovery
        recoilX += (0f - recoilX) * (1f - FastMath.exp(-tpf * recoverySpeed));
        recoilY += (0f - recoilY) * (1f - FastMath.exp(-tpf * recoverySpeed));
        recoilZ += (0f - recoilZ) * (1f - FastMath.exp(-tpf * recoverySpeed));

        cameraOffset.addLocal(
                recoilX * weight,
                recoilY * weight,
                recoilZ * weight
        );

        if (Math.abs(recoilX) < 0.0001f &&
                Math.abs(recoilY) < 0.0001f &&
                Math.abs(recoilZ) < 0.0001f &&
                Math.abs(velY) < 0.0001f &&
                Math.abs(velZ) < 0.0001f &&
                Math.abs(rotPitch) < 0.0001f &&
                Math.abs(rotYaw) < 0.0001f &&
                Math.abs(rotVelPitch) < 0.0001f &&
                Math.abs(rotVelYaw) < 0.0001f
        ) {
            cleanup = true;
        }
    }

    @Override
    public void applyRotation(Quaternion cameraRotation) {
        float tpf = 1f / FIXED_FRAMERATE;

        // integrate rotation recoil
        rotPitch += rotVelPitch;
        rotYaw   += rotVelYaw;

        // damping
        rotVelPitch *= (1f - tpf * damping);
        rotVelYaw   *= (1f - tpf * damping);

        // recovery to neutral
        rotPitch += (0f - rotPitch) * (1f - FastMath.exp(-tpf * recoverySpeed));
        rotYaw   += (0f - rotYaw) * (1f - FastMath.exp(-tpf * recoverySpeed));

        Quaternion q = new Quaternion();
        q.fromAngles(
                -rotPitch * weight,
                rotYaw * weight,
                0f
        );

        cameraRotation.multLocal(q);
    }
}