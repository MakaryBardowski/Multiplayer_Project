package pocs;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.system.player.component.camera.model.TransformEffect;

import static com.mccurry.game.gameplay.sharedcomponents.transformeffect.TransformEffectConstants.FIXED_FRAMERATE;
public class SmgRecoilTransformEffect extends TransformEffect {

    private final float fadeSpeed = 20f;

    // tuned impulses
    private final float upKick = 0.0085f;     // ↓ reduced
    private final float sideKick = 0.0045f;   // ↑ slightly stronger

    // control
    private final float recoverySpeed = 11f;  // faster return
    private final float damping = 16f;        // tighter control

    private float recoilX = 0f;
    private float recoilY = 0f;

    private float velX = 0f;
    private float velY = 0f;

    private float time = 0f;

    private void updateWeight(float tpf) {
        weight += (targetWeight - weight) * (1f - FastMath.exp(-tpf * fadeSpeed));
    }

    public void onShot() {
        // reduced vertical stacking
        velY += upKick;

        // smoother, slightly stronger sideways motion
        float drift = (float) Math.sin(time * 1.6f);
        velX += drift * sideKick;

        targetWeight = 1f;
    }

    @Override
    public void applyPosition(Vector3f cameraOffset) {
        float tpf = 1f / FIXED_FRAMERATE;

        updateWeight(tpf);
        time += tpf;

        // integrate
        recoilX += velX;
        recoilY += velY;

        // --- SOFT CAP ON VERTICAL (prevents infinite climb) ---
        float maxUp = 0.12f;
        if (recoilY > maxUp) {
            recoilY = FastMath.interpolateLinear(0.2f, recoilY, maxUp);
        }

        // damping
        velX *= (1f - tpf * damping);
        velY *= (1f - tpf * damping);

        // recovery
        recoilX += (0f - recoilX) * (1f - FastMath.exp(-tpf * recoverySpeed));
        recoilY += (0f - recoilY) * (1f - FastMath.exp(-tpf * recoverySpeed));

        cameraOffset.addLocal(
                recoilX * weight,
                recoilY * weight,
                0
        );

        // cleanup
        if (Math.abs(recoilX) < 0.0001f &&
                Math.abs(recoilY) < 0.0001f &&
                Math.abs(velX) < 0.0001f &&
                Math.abs(velY) < 0.0001f &&
                targetWeight == 0f) {
            cleanup = true;
        }
    }

    @Override
    public void applyRotation(Quaternion cameraRotation) {
        float pitch = -recoilY * 2.3f; // slightly reduced
        float yaw   = recoilX * 2.2f;  // slightly stronger sideways feel

        Quaternion q = new Quaternion();
        q.fromAngles(pitch * weight, yaw * weight, 0f);
        cameraRotation.multLocal(q);
    }
}