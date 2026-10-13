package com.mccurry.game.gameplay.system.crosshair.model;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.mccurry.game.gameplay.system.crosshair.control.CrosshairControl;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class Crosshair extends Node {

    @Nullable private final Spatial rightSide;
    @Nullable private final Spatial leftSide;

    @Nullable private Vector3f initialRightSidePos;
    @Nullable private Vector3f initialLeftSidePos;

    private final float minSpreadPx;
    private final float maxSpreadPx;

    private float currentSpreadPx = 0f;
    private float targetSpreadPx = 0f;

    private final float interpolationSpeedPxPerSec;

    public Crosshair(@Nullable Spatial rightSide, @Nullable Spatial leftSide, float interpolationSpeedPxPerSec, float minSpreadPx, float maxSpreadPx) {
        this.rightSide = rightSide;
        this.leftSide = leftSide;

        this.interpolationSpeedPxPerSec = interpolationSpeedPxPerSec;
        this.minSpreadPx = minSpreadPx;
        this.maxSpreadPx = maxSpreadPx;

        if (rightSide != null) {
            initialRightSidePos = rightSide.getLocalTranslation().clone();
        }

        if (leftSide != null) {
            initialLeftSidePos = leftSide.getLocalTranslation().clone();
        }

        addControl(new CrosshairControl(this));
    }

    public void targetSpread(float spreadAmountPx) {
        setTargetSpreadPx(spreadAmountPx);
    }

}