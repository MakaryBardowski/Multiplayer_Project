package com.mccurry.game.gameplay.system.animation.control;

import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.mccurry.game.gameplay.system.raycast.entityraycast.model.EntityRaycastHitbox;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ManualAnimationControl extends AbstractControl {

    private final Map<EntityRaycastHitbox, Joint> hitTargets = new HashMap<>();
    private final Map<Joint, JointTransformState> states = new HashMap<>();

    @Override
    protected void controlUpdate(float deltaTime) {
        for (var entry : states.entrySet()) {

            Joint joint = entry.getKey();
            JointTransformState state = entry.getValue();

            state.currentRotation.slerp(
                    state.targetRotation,
                    deltaTime * state.rotationSpeed
            );

            state.currentTranslation.interpolateLocal(
                    state.targetTranslation,
                    deltaTime * state.translationSpeed
            );

            joint.setLocalRotation(state.currentRotation);
            joint.setLocalTranslation(state.currentTranslation);
        }
    }

    public void registerHitTarget(EntityRaycastHitbox hitbox, Joint joint) {
        hitTargets.put(hitbox, joint);
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {

    }

    public void rotateBy(Joint joint, Quaternion rotation) {
        state(joint).targetRotation.multLocal(rotation);
    }

    public void translateBy(Joint joint, Vector3f translation) {
        state(joint).targetTranslation.addLocal(translation);
    }

    private JointTransformState state(Joint joint) {
        return states.computeIfAbsent(joint, j -> {
            JointTransformState s = new JointTransformState();

            s.currentRotation.set(j.getLocalRotation());
            s.targetRotation.set(j.getLocalRotation());

            s.currentTranslation.set(j.getLocalTranslation());
            s.targetTranslation.set(j.getLocalTranslation());

            return s;
        });
    }

    public void rotateBy(EntityRaycastHitbox hitbox, Quaternion rotation) {
        Joint joint = hitTargets.get(hitbox);
        if (joint != null) {
            rotateBy(joint, rotation);
        }
    }

    public void translateBy(EntityRaycastHitbox hitbox, Vector3f translation) {
        Joint joint = hitTargets.get(hitbox);
        if (joint != null) {
            translateBy(joint, translation);
        }
    }

    public static class JointTransformState {

        Quaternion targetRotation = new Quaternion();
        Quaternion currentRotation = new Quaternion();

        Vector3f targetTranslation = new Vector3f();
        Vector3f currentTranslation = new Vector3f();

        float rotationSpeed = 8f;
        float translationSpeed = 8f;
    }
}
