package com.mccurry.game.gameplay.system.crosshair.model;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.mccurry.game.gameplay.system.crosshair.control.HitmarkerControl;

public class Hitmarker extends Node {
    private Geometry hitmarkerGeom;
    private HitmarkerControl hitmarkerControl;

    public Hitmarker(AssetManager assetManager){
        var texPath = "Interface/GUI/Crosshair/hitmarker/hitmarker.png";
        var tex = assetManager.loadTexture(texPath);
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        tex.setMagFilter(Texture.MagFilter.Nearest);

        var image = tex.getImage();
        float width = image.getWidth();
        float height = image.getHeight();
        var centerQuad = new Quad(width, height);

        var centerGeom = new Geometry("TexturedQuad", centerQuad);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        mat.setColor("Color", ColorRGBA.White.clone().setAlpha(0.5f));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        centerGeom.setMaterial(mat);

        hitmarkerGeom = centerGeom;
        hitmarkerControl = new HitmarkerControl(this,hitmarkerGeom);
        centerGeom.addControl(hitmarkerControl);

        this.attachChild(hitmarkerGeom);

        hitmarkerGeom.move( -(int) (centerQuad.getWidth()/2)+1, 0, 0);
        hitmarkerGeom.move(0, -(int)(centerQuad.getHeight()/2), 0);

    }

    public void initialize(boolean isKillingBlow){
        hitmarkerControl.initialize(isKillingBlow);
    }
}
