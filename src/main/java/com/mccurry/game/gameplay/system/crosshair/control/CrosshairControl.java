package com.mccurry.game.gameplay.system.crosshair.control;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.mccurry.game.gameplay.system.crosshair.model.Crosshair;
import lombok.RequiredArgsConstructor;

import static com.mccurry.game.gameplay.core.ecs.CoreConstants.DELTA_TIME_60_FPS;

@RequiredArgsConstructor
public class CrosshairControl extends AbstractControl {
    private final Crosshair crosshair;

    @Override
    protected void controlUpdate(float tpf) {
        if (!isEnabled()) {
            return;
        }

        // Recover toward zero.
        crosshair.setTargetSpreadPx(
                Math.clamp(
                        crosshair.getTargetSpreadPx() + crosshair.getInterpolationSpeedPxPerSec() * DELTA_TIME_60_FPS,
                        crosshair.getMinSpreadPx(),
                        crosshair.getMaxSpreadPx()
                )
        );

        // Smoothly move current spread toward target.
        var currentSpreadIncrement = (crosshair.getTargetSpreadPx() - crosshair.getCurrentSpreadPx()) * Math.min(1f, DELTA_TIME_60_FPS * 2260f);
        crosshair.setCurrentSpreadPx(crosshair.getCurrentSpreadPx() + currentSpreadIncrement);


        applySpread(crosshair.getCurrentSpreadPx());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

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
