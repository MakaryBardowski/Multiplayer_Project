package com.mccurry.game.gameplay.system.crosshair;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameplay.system.crosshair.event.CrosshairChangeRequestEvent;
import com.mccurry.game.gameplay.system.crosshair.event.CrosshairSpreadRequestEvent;
import com.mccurry.game.gameplay.system.crosshair.model.Crosshair;
import com.mccurry.game.gameplay.system.crosshair.model.CrosshairType;
import lombok.NonNull;

import java.util.Random;

public class CrosshairSystem implements EventSubscriber {

    private final AssetManager assetManager;
    private final Node crosshairParentNode; // todo: have some cleanup INSIDE system
    private final SyncEventHandler<CrosshairChangeRequestEvent> onCrosshairChanged;
    private final SyncEventHandler<CrosshairSpreadRequestEvent> onCrosshairSpread;

    private final Crosshair defaultCrosshair;
    private Crosshair currentCrosshair;

    public CrosshairSystem(AssetManager assetManager, Node crosshairParentNode) {
        this.assetManager = assetManager;
        this.crosshairParentNode = crosshairParentNode;

        defaultCrosshair = buildCrosshair(CrosshairType.DEFAULT);
        currentCrosshair = defaultCrosshair;

        onCrosshairChanged = (event, lifecycleManager) -> {
            var type = event.getNewCrosshairType();
            currentCrosshair.removeFromParent();
            currentCrosshair = buildCrosshair(type);
            crosshairParentNode.attachChild(currentCrosshair);
            float x = 320;
            float y = 180;

            currentCrosshair.setLocalTranslation(x,y,0);
        };

        onCrosshairSpread = (event, lifecycleManager) -> {
            currentCrosshair.spread(4,15);
        };
    }


    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(CrosshairChangeRequestEvent.class, onCrosshairChanged);
        eventBus.subscribe(CrosshairSpreadRequestEvent.class, onCrosshairSpread);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(CrosshairChangeRequestEvent.class, onCrosshairChanged);
        eventBus.unsubscribe(CrosshairSpreadRequestEvent.class, onCrosshairSpread);
    }

    private Crosshair buildCrosshair(CrosshairType crosshairType) {
        var right = crosshairType.getRightTexturePath();
        var center = crosshairType.getCenterTexturePath();
        var left = crosshairType.getLeftTexturePath();

        Geometry rightSideGeom = null;
        Geometry centerGeom = null;
        Geometry leftSideGeom = null;


        Quad centerQuad = null;

        if (center != null) {
            var tex = assetManager.loadTexture(center);
            tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
            tex.setMagFilter(Texture.MagFilter.Nearest);

            var image = tex.getImage();
            float width = image.getWidth();
            float height = image.getHeight();
            centerQuad = new Quad(width, height);

            centerGeom = new Geometry("TexturedQuad", centerQuad);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setTexture("ColorMap", tex);
            mat.setColor("Color", ColorRGBA.White);
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            centerGeom.setMaterial(mat);

        }

        if (right != null) {
            var tex = assetManager.loadTexture(right);
            tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
            tex.setMagFilter(Texture.MagFilter.Nearest);

            var image = tex.getImage();
            float width = image.getWidth();
            float height = image.getHeight();
            var quad = new Quad(width, height);

            rightSideGeom = new Geometry("TexturedQuad", quad);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setTexture("ColorMap", tex);
            mat.setColor("Color", ColorRGBA.White);
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            rightSideGeom.setMaterial(mat);

            rightSideGeom.move(0, -(int)(quad.getHeight()/2), 0);

            if (centerQuad != null) {
                rightSideGeom.move(centerQuad.getWidth(), 0, 0);
            }
        }


        if (left != null) {
            var tex = assetManager.loadTexture(left);
            tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
            tex.setMagFilter(Texture.MagFilter.Nearest);

            var image = tex.getImage();
            float width = image.getWidth();
            float height = image.getHeight();
            var quad = new Quad(width, height);

            leftSideGeom = new Geometry("TexturedQuad", quad);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setTexture("ColorMap", tex);
            mat.setColor("Color", ColorRGBA.White);
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            leftSideGeom.setMaterial(mat);




            leftSideGeom.move(0, -(int)(quad.getHeight()/2), 0);


            if (centerQuad != null) {
                leftSideGeom.move( -quad.getWidth(), 0, 0);
            }
        }

        var crosshair = new Crosshair(rightSideGeom,leftSideGeom);

        if(centerGeom != null){
            crosshair.attachChild(centerGeom);
        }

        if(leftSideGeom != null){
            crosshair.attachChild(leftSideGeom);
        }

        if(rightSideGeom != null){
            crosshair.attachChild(rightSideGeom);
        }

        return crosshair;
    }
}
