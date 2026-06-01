package com.mccurry.game.gameplay.system.crosshair.model;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.jetbrains.annotations.Nullable;

public class Crosshair extends Node {
    private static final float RECOVERY_SPEED = 20f; // px/sec

    @Nullable private final Spatial rightSide;
    @Nullable private final Spatial leftSide;

    private Vector3f initialRightSidePos;
    private Vector3f initialLeftSidePos;

    private float currentSpread = 0f;
    private float targetSpread = 0f;

    public Crosshair(@Nullable Spatial rightSide, @Nullable Spatial leftSide) {
        this.rightSide = rightSide;
        this.leftSide = leftSide;

        if (rightSide != null) {
            initialRightSidePos = rightSide.getLocalTranslation().clone();
        }

        if (leftSide != null) {
            initialLeftSidePos = leftSide.getLocalTranslation().clone();
        }

        addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                // Recover toward zero.
                targetSpread = Math.max(0f, targetSpread - RECOVERY_SPEED * tpf);

                // Smoothly move current spread toward target.
                currentSpread += (targetSpread - currentSpread) * Math.min(1f, tpf * 2260f);

                applySpread(currentSpread);
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });
    }

    public void spread(float spreadAmountPx, float maxSpread) {
        targetSpread = Math.min(
                maxSpread,
                targetSpread + spreadAmountPx
        );
    }

    private void applySpread(float spread) {

        if (rightSide != null) {
            rightSide.setLocalTranslation(
                    initialRightSidePos.x + spread,
                    initialRightSidePos.y,
                    initialRightSidePos.z
            );
        }

        if (leftSide != null) {
            leftSide.setLocalTranslation(
                    initialLeftSidePos.x - spread,
                    initialLeftSidePos.y,
                    initialLeftSidePos.z
            );
        }
    }
}