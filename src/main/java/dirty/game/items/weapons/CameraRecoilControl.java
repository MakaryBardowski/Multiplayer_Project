package dirty.game.items.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dirty.game.cameraAndInput.ImprovedNoise;

public class CameraRecoilControl extends RecoilControl {
    private float xperlin = 6;
    private float yperlin = 6;
    private float shakeStrength;
    public CameraRecoilControl(float kickback, float recoilX, float recoilY, float recoilZ, float sideRecoil, float upRecoil, float backRecoil) {
        super(kickback, recoilX, recoilY, recoilZ, 20, sideRecoil, upRecoil, backRecoil);
    }

    public CameraRecoilControl(float kickback, float recoilX, float recoilY, float recoilZ, int snap, float sideRecoil, float upRecoil, float backRecoil) {
        super(kickback, recoilX, recoilY, recoilZ, snap, sideRecoil, upRecoil, backRecoil);
    }

    public CameraRecoilControl(float kickback, float recoilX, float recoilY, float recoilZ, int snap,float shakeStrength, float sideRecoil, float upRecoil, float backRecoil) {
        super(kickback, recoilX, recoilY, recoilZ, snap, sideRecoil, upRecoil, backRecoil);
        this.shakeStrength = shakeStrength;
    }

    @Override
    protected void controlUpdate(float tpf) {
        this.recoilUpdate();
    }

    @Override
    public void recoilUpdate() {

        targetRotationRecoil.nlerp(new Quaternion(0, 0, 0, 1), RECOIL_SPEED * snap * .4f);
        currentRotationRecoil.nlerp(targetRotationRecoil, RECOIL_SPEED * snap);
        spatial.setLocalRotation(currentRotationRecoil);
    }

    @Override
    public void recoilFire() {

        rotationRecoilX = FastMath.PI / (40 + random.nextFloat(10));

        rotationRecoilY = (float) ImprovedNoise.noise(xperlin, yperlin);
        rotationRecoilZ = (float) ImprovedNoise.noise(xperlin, yperlin);

        xperlin = random.nextFloat(-1, 1) * 30;
        yperlin = random.nextFloat(-1, 1) * 30;

        float scale = 0.05f;
        rotationRecoilY = rotationRecoilY * scale * shakeStrength; // left right
        rotationRecoilX = rotationRecoilX * shakeStrength;
//        recoilZ = recoilZ * 0.005f; // rotates head 

//        targetRotationRecoil.addLocal((new Quaternion()).fromAngleAxis(FastMath.PI / (20-kickback), new Vector3f(recoilX*4, getRandomNumber(-recoilY, recoilY), getRandomNumber(-recoilZ, recoilZ))));
        targetRotationRecoil.addLocal(
                (new Quaternion()).fromAngleAxis(
                        -rotationRecoilX,
                        new Vector3f(rotationRecoilX,
                                rotationRecoilY,
                                0
                        )));

    }

    //    @Override
//    public void recoilUpdate() {
//        targetRotationRecoil.nlerp(new Quaternion(0, 0, 0, 1), RECOIL_SPEED*snap*.4f);
//        currentRotationRecoil.slerp(targetRotationRecoil, RECOIL_SPEED * snap);
//        spatial.setLocalRotation(currentRotationRecoil);
//    }
///    @Override
////    public void recoilFire(){
////        targetRotationRecoil.addLocal((new Quaternion()).fromAngleAxis(FastMath.PI / (20-kickback), new Vector3f(recoilX*4, getRandomNumber(-recoilY, recoilY), getRandomNumber(-recoilZ, recoilZ))));
////    }
}
