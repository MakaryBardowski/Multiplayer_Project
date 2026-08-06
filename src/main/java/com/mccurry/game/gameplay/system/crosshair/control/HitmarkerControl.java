package com.mccurry.game.gameplay.system.crosshair.control;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class HitmarkerControl extends AbstractControl {

    private static final float COLOR_FADE_DURATION = 0.08f;
    private static final float POP_SCALE = 1.35f;
    private static final float POP_DURATION = 0.10f;
    private static final float ALPHA_FADE_SPEED = 4f;

    private final ColorRGBA defaultHitmarkerColor;
    private final ColorRGBA mutableHitmarkerColor;
    private final ColorRGBA targetColor = new ColorRGBA();

    private final Node hitmarkerNode;
    private final Vector3f defaultScale = new Vector3f();

    private float noColorFalloffRemainingDurationSeconds;
    private float colorFadeRemainingSeconds;
    private float popRemainingSeconds;

    private boolean popping;

    public HitmarkerControl(Node hitmarkerGeomParent, Geometry hitmarkerGeom) {
        this.hitmarkerNode = hitmarkerGeomParent;

        mutableHitmarkerColor = (ColorRGBA) hitmarkerGeom
                .getMaterial()
                .getParam("Color")
                .getValue();

        defaultHitmarkerColor = mutableHitmarkerColor.clone();
        targetColor.set(defaultHitmarkerColor);

        defaultScale.set(hitmarkerNode.getLocalScale());
    }

    @Override
    protected void controlUpdate(float tpf) {

        // Fade color to the desired color.
        if (colorFadeRemainingSeconds > 0f) {
            float t = Math.min(1f, tpf / colorFadeRemainingSeconds);
            mutableHitmarkerColor.interpolateLocal(targetColor, t);
            colorFadeRemainingSeconds -= tpf;
        }

        // Pop back to the default scale.
        if (popping) {
            float t = Math.min(1f, tpf / popRemainingSeconds);

            hitmarkerNode.getLocalScale().interpolateLocal(defaultScale, t);

            popRemainingSeconds -= tpf;
            if (popRemainingSeconds <= 0f) {
                hitmarkerNode.setLocalScale(defaultScale);
                popping = false;
            }
        }

        // Hold before fading out.
        noColorFalloffRemainingDurationSeconds -= tpf;
        if (noColorFalloffRemainingDurationSeconds > 0f) {
            return;
        }

        float currentAlpha = mutableHitmarkerColor.a - tpf * ALPHA_FADE_SPEED;
        if (currentAlpha <= 0f) {
            hitmarkerNode.removeFromParent();
            return;
        }

        mutableHitmarkerColor.a = currentAlpha;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void initialize(boolean isKillingBlow) {
        noColorFalloffRemainingDurationSeconds = 0.5f;

        // Smoothly transition to the desired color.
        colorFadeRemainingSeconds = COLOR_FADE_DURATION;
        targetColor.set(isKillingBlow ? ColorRGBA.Red : defaultHitmarkerColor);

        // Restart the pop animation.
        hitmarkerNode.setLocalScale(defaultScale.x * POP_SCALE,
                defaultScale.y * POP_SCALE,
                defaultScale.z * POP_SCALE);

        popRemainingSeconds = POP_DURATION;
        popping = true;
    }
}