package com.mccurry.game.gameplay.system.crosshair.model;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.mccurry.game.gameplay.system.crosshair.control.CrosshairControl;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class Crosshair extends Node {
    private final float RECOVERY_SPEED = 20f; // px/sec

    @Nullable private final Spatial rightSide;
    @Nullable private final Spatial leftSide;

    @Nullable private Vector3f initialRightSidePos;
    @Nullable private Vector3f initialLeftSidePos;

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

        addControl(new CrosshairControl(this));
    }

    public void spread(float spreadAmountPx, float maxSpread) {
        targetSpread = Math.min(
                maxSpread,
                targetSpread + spreadAmountPx
        );
    }

}