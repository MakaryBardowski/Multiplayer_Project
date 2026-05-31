package com.mccurry.game.gameplay.system.crosshair.model;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.jetbrains.annotations.Nullable;

public class Crosshair extends Node {
    @Nullable private final Spatial rightSide;
    @Nullable private final Spatial leftSide;

    @Nullable private Vector3f initialRightSidePos;
    @Nullable private Vector3f initalLeftSidePos;

    public Crosshair(@Nullable Spatial rightSide, @Nullable Spatial leftSide){
        this.rightSide = rightSide;
        this.leftSide = leftSide;

        if(rightSide != null){
            initialRightSidePos = rightSide.getLocalTranslation().clone();
        }

        if(leftSide != null){
            initalLeftSidePos = leftSide.getLocalTranslation().clone();
        }

    }

    private void spread(float spreadOutTimeSeconds, float spreadInTimeSeconds){

    }
}
