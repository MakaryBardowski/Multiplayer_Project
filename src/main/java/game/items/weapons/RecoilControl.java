package game.items.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;

public class RecoilControl extends AbstractControl {

    protected Random random = new Random();
    protected static final float RECOIL_SPEED = 0.016f;
    protected float rotationRecoilX = 0f;
    protected float rotationRecoilY = 0f;
    protected float rotationRecoilZ = 0f;
    protected int snap; // 30 feels strong
    protected float kickback;

    protected Quaternion currentRotationRecoil = new Quaternion(0, 0, 0, 1);
    protected Quaternion targetRotationRecoil = new Quaternion(0, 0, 0, 1);
    protected Vector3f currentPositionRecoil = new Vector3f(0, 0, 0);
    protected Vector3f targetPositionRecoil = new Vector3f(0, 0, 0);

    protected float sideRecoil;
    protected float upRecoil;
    protected float backRecoil;

    @Getter
    @Setter
    protected float rotationReturnSpeed = 1f;

    @Getter
    @Setter
    protected float rotationSpeed = 1f;

    @Getter
    @Setter
    protected float translationReturnSpeed = 1f;

    @Getter
    @Setter
    protected float translationSpeed = 1f;

    public RecoilControl(float kickback, float rotationRecoilX, float rotationRecoilY, float rotationRecoilZ, int snap, float upRecoil, float sideRecoil) {
        this(kickback, rotationRecoilX, rotationRecoilY, rotationRecoilZ, snap, sideRecoil, upRecoil, 0);
    }

    public RecoilControl(float kickback, float rotationRecoilX, float rotationRecoilY, float rotationRecoilZ, int snap, float sideRecoil, float upRecoil, float backRecoil) {
        this.kickback = kickback;
        this.rotationRecoilX = rotationRecoilX;
        this.rotationRecoilY = rotationRecoilY;
        this.rotationRecoilZ = rotationRecoilZ;
        this.snap = snap;
        this.sideRecoil = sideRecoil;
        this.upRecoil = upRecoil;
        this.backRecoil = backRecoil;
    }

    // better recoil for any type of weapon
    public void recoilUpdate() {
        targetRotationRecoil.nlerp(new Quaternion(0, 0, 0, 1), RECOIL_SPEED * snap * rotationReturnSpeed);
        currentRotationRecoil.slerp(targetRotationRecoil, RECOIL_SPEED * snap * rotationSpeed);

        targetPositionRecoil.interpolateLocal(new Vector3f(0, 0, 0), RECOIL_SPEED * snap * translationReturnSpeed);
        currentPositionRecoil.interpolateLocal(targetPositionRecoil, RECOIL_SPEED * snap * translationSpeed);

        spatial.setLocalRotation(currentRotationRecoil);
        spatial.setLocalTranslation(currentPositionRecoil);

    }

    public void recoilFire() {
        targetRotationRecoil = targetRotationRecoil.add(new Quaternion().fromAngleAxis(
                        FastMath.PI / (6 - kickback),
                        new Vector3f(
                                rotationRecoilX,
                                getRandomNumber(-rotationRecoilY, rotationRecoilY),
                                getRandomNumber(-rotationRecoilZ, rotationRecoilZ))
                )
        );
        System.out.println(" UP RECOIL "+ upRecoil);
        System.out.println(" SIDE RECOIL "+ sideRecoil);
        System.out.println(" SIDE RECOIL "+ backRecoil);
        targetPositionRecoil = targetPositionRecoil.subtract(targetRotationRecoil.getRotationColumn(2).add(sideRecoil, upRecoil, backRecoil));
    }

    public float getRandomNumber(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public float getRotationRecoilX() {
        return rotationRecoilX;
    }

    public void setRotationRecoilX(float rotationRecoilX) {
        this.rotationRecoilX = rotationRecoilX;
    }

    public float getRotationRecoilY() {
        return rotationRecoilY;
    }

    public void setRotationRecoilY(float rotationRecoilY) {
        this.rotationRecoilY = rotationRecoilY;
    }

    public float getRotationRecoilZ() {
        return rotationRecoilZ;
    }

    public void setRotationRecoilZ(float rotationRecoilZ) {
        this.rotationRecoilZ = rotationRecoilZ;
    }

    public Quaternion getCurrentRotationRecoil() {
        return currentRotationRecoil;
    }

    public void setCurrentRotationRecoil(Quaternion currentRotationRecoil) {
        this.currentRotationRecoil = currentRotationRecoil;
    }

    public Quaternion getTargetRotationRecoil() {
        return targetRotationRecoil;
    }

    public void setTargetRotationRecoil(Quaternion targetRotationRecoil) {
        this.targetRotationRecoil = targetRotationRecoil;
    }

    public Vector3f getCurrentPositionRecoil() {
        return currentPositionRecoil;
    }

    public void setCurrentPositionRecoil(Vector3f currentPositionRecoil) {
        this.currentPositionRecoil = currentPositionRecoil;
    }

    public Vector3f getTargetPositionRecoil() {
        return targetPositionRecoil;
    }

    public void setTargetPositionRecoil(Vector3f targetPositionRecoil) {
        this.targetPositionRecoil = targetPositionRecoil;
    }

    @Override
    protected void controlUpdate(float tpf) {
        recoilUpdate();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //
    }

}
