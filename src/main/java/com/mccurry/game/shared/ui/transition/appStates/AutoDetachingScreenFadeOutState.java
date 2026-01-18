package com.mccurry.game.shared.ui.transition.appStates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.mccurry.game.shared.settings.model.ResolutionSettings;

public class AutoDetachingScreenFadeOutState extends BaseAppState {
    private final float maxFadeOutTimeSeconds;

    private float currentFadeOutTime;
    private final ColorRGBA currentOverlayColor = ColorRGBA.Black.clone().setAlpha(1);

    private AppStateManager stateManager;

    private Geometry overlayGeom;

    public AutoDetachingScreenFadeOutState(float maxFadeOutTimeSeconds) {
        this.maxFadeOutTimeSeconds = maxFadeOutTimeSeconds;
        currentFadeOutTime = maxFadeOutTimeSeconds;
    }

    @Override
    protected void initialize(Application application) {
        stateManager = getStateManager();

        var simpleApplication = (SimpleApplication) application;
        var baseResolution = ResolutionSettings.R_640x360;

        var quad = new Quad(baseResolution.getWidth(), baseResolution.getHeight());
        overlayGeom = new Geometry("BlackQuad", quad);

        var mat = new Material(simpleApplication.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", currentOverlayColor);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        overlayGeom.setMaterial(mat);
        overlayGeom.setQueueBucket(RenderQueue.Bucket.Gui);
        overlayGeom.getLocalTranslation().setZ(1000000f);
        simpleApplication.getGuiNode().attachChild(overlayGeom);

        System.out.println("[AutoDetachingScreenFadeInState] Starting fade out...");
    }

    @Override
    public void update(float tpf) {
        currentFadeOutTime -=tpf;
        currentOverlayColor.setAlpha(currentFadeOutTime / maxFadeOutTimeSeconds);
        if(currentFadeOutTime <= 0){

            stateManager.detach(this);
        }
    }

    @Override
    protected void cleanup(Application application) {
        overlayGeom.removeFromParent();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
