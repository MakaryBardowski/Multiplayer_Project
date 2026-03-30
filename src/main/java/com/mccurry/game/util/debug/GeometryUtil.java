package com.mccurry.game.util.debug;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.debug.Arrow;

public class GeometryUtil {
    private static Geometry createGeometry(Mesh shape, ColorRGBA color, AssetManager a) {
        Geometry g = new Geometry("coordinate axis", shape);
        Material mat = new Material(a, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setLineWidth(4);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        return g;
    }

    public static Geometry createArrow(float arrowLength, Vector3f direction, ColorRGBA colorRGBA, AssetManager assetManager) {
        var arrow = new Arrow(direction.normalize().mult(arrowLength));
        return createGeometry(arrow, colorRGBA, assetManager);
    }
}
