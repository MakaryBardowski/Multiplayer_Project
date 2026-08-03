package com.mccurry.game.gameplay.system.animation.control;

import com.jme3.anim.Joint;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.mccurry.game.gameplay.system.raycast.entityraycast.model.EntityRaycastHitbox;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ManualAnimationControl extends AbstractControl {

    private final Map<EntityRaycastHitbox, Joint> hitTargets = new HashMap<>();
    private final Map<Joint, JointTransformState> states = new HashMap<>();

    private static final Quaternion IDENTITY = new Quaternion();
    private static final Vector3f ZERO = new Vector3f();


    @Override
    protected void controlUpdate(float deltaTime) {

        for (var entry : states.entrySet()) {

            Joint joint = entry.getKey();
            JointTransformState state = entry.getValue();


            /*
             * Remove our previous frame contribution.
             * What remains is the animation/default pose.
             */
            if (!state.appliedRotation.equals(IDENTITY)) {
                joint.setLocalRotation(
                        new Quaternion(joint.getLocalRotation())
                                .multLocal(state.appliedRotation.inverse())
                );
            }

            if (!state.appliedTranslation.equals(ZERO)) {
                joint.setLocalTranslation(
                        new Vector3f(joint.getLocalTranslation())
                                .subtractLocal(state.appliedTranslation)
                );
            }


            /*
             * Temporary effects decay back to neutral.
             */
            state.temporaryRotation.slerp(
                    IDENTITY,
                    deltaTime * state.temporaryReturnSpeed
            );

            state.temporaryTranslation.interpolateLocal(
                    ZERO,
                    deltaTime * state.temporaryReturnSpeed
            );


            /*
             * Smooth permanent layer.
             */
            state.currentRotation.slerp(
                    state.targetRotation,
                    deltaTime * state.rotationSpeed
            );

            state.currentTranslation.interpolateLocal(
                    state.targetTranslation,
                    deltaTime * state.translationSpeed
            );


            /*
             * Combine manual layers.
             */
            Quaternion manualRotation = new Quaternion()
                    .set(state.currentRotation)
                    .multLocal(state.temporaryRotation);

            Vector3f manualTranslation = new Vector3f(
                    state.currentTranslation
            ).addLocal(state.temporaryTranslation);


            /*
             * Apply manual layer.
             */
            joint.setLocalRotation(
                    new Quaternion(joint.getLocalRotation())
                            .multLocal(manualRotation)
            );

            joint.setLocalTranslation(
                    new Vector3f(joint.getLocalTranslation())
                            .addLocal(manualTranslation)
            );


            /*
             * Remember what we added so we can remove it next frame.
             */
            state.appliedRotation.set(manualRotation);
            state.appliedTranslation.set(manualTranslation);
        }
    }


    public void registerHitTarget(EntityRaycastHitbox hitbox, Joint joint) {
        hitTargets.put(hitbox, joint);
    }


    // ------------------------------------------------------------------------
    // Persistent offsets
    // ------------------------------------------------------------------------

    public void rotateBy(Joint joint, Quaternion rotation) {
        state(joint).targetRotation.multLocal(rotation);
    }


    public void translateBy(Joint joint, Vector3f translation) {
        state(joint).targetTranslation.addLocal(translation);
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


    // ------------------------------------------------------------------------
    // Temporary offsets
    // ------------------------------------------------------------------------

    public void addRotationTemporary(Joint joint, Quaternion rotation) {
        state(joint).temporaryRotation.multLocal(rotation);
    }


    public void addTranslationTemporary(Joint joint, Vector3f translation) {
        state(joint).temporaryTranslation.addLocal(translation);
    }


    public void addRotationTemporary(
            EntityRaycastHitbox hitbox,
            Quaternion rotation
    ) {
        Joint joint = hitTargets.get(hitbox);

        if (joint != null) {
            addRotationTemporary(joint, rotation);
        }
    }


    public void addTranslationTemporary(
            EntityRaycastHitbox hitbox,
            Vector3f translation
    ) {
        Joint joint = hitTargets.get(hitbox);

        if (joint != null) {
            addTranslationTemporary(joint, translation);
        }
    }


    private JointTransformState state(Joint joint) {
        return states.computeIfAbsent(
                joint,
                j -> new JointTransformState()
        );
    }


    @Override
    protected void controlRender(
            RenderManager renderManager,
            ViewPort viewPort
    ) {

    }


    @ToString
    public static class JointTransformState {

        // Persistent procedural offsets
        Quaternion targetRotation = new Quaternion();
        Vector3f targetTranslation = new Vector3f();

        // Smoothed persistent offsets
        Quaternion currentRotation = new Quaternion();
        Vector3f currentTranslation = new Vector3f();


        // Temporary effects
        Quaternion temporaryRotation = new Quaternion();
        Vector3f temporaryTranslation = new Vector3f();


        // What we added last frame
        Quaternion appliedRotation = new Quaternion();
        Vector3f appliedTranslation = new Vector3f();


        float rotationSpeed = 1f;
        float translationSpeed = 8f;

        float temporaryReturnSpeed = 1f;
    }
}