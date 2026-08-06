package com.mccurry.game.gameplay.system.crosshair.control;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.mccurry.game.gameplay.system.crosshair.model.Crosshair;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrosshairControl extends AbstractControl {
    private final Crosshair crosshair;

    @Override
    protected void controlUpdate(float tpf) {
        if(!isEnabled()){
            return;
        }

        // Recover toward zero.
        crosshair.setTargetSpread(Math.max(0f, crosshair.getTargetSpread() - crosshair.getRECOVERY_SPEED() * tpf));

        // Smoothly move current spread toward target.
        var currentSpreadIncrement = (crosshair.getTargetSpread() - crosshair.getCurrentSpread()) * Math.min(1f, tpf * 2260f);;
        crosshair.setCurrentSpread(
                crosshair.getCurrentSpread() + currentSpreadIncrement
        );


        applySpread(crosshair.getCurrentSpread());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

    private void applySpread(float spread) {
        var rightSide = crosshair.getRightSide();
        if (rightSide != null) {
            rightSide.setLocalTranslation(
                    crosshair.getInitialRightSidePos().x + spread,
                    crosshair.getInitialRightSidePos().y,
                    crosshair.getInitialRightSidePos().z
            );
        }

        var leftSide = crosshair.getLeftSide();
        if (leftSide != null) {
            leftSide.setLocalTranslation(
                    crosshair.getInitialLeftSidePos().x - spread,
                    crosshair.getInitialLeftSidePos().y,
                    crosshair.getInitialLeftSidePos().z
            );
        }
    }
}
