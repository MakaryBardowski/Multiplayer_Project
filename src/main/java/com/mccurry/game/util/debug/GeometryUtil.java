package com.mccurry.game.util.debug;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.ShadowUtil;

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

    public static Node createUnshadedBox(AssetManager assetManager) {
        Box box2 = new Box(0.2f, 0.2f, 0.2f);
        Geometry red = new Geometry("Box", box2);
        Material mat2 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        red.setMaterial(mat2);
        Node n = new Node();
        n.attachChild(red);
        return n;
    }

    public static Geometry createViewingFrustum(Camera cam, AssetManager assetManager) {
        Vector3f[] points = new Vector3f[8];
        for (int i = 0; i < 8; i++) {
            points[i] = new Vector3f();
        }

        ShadowUtil.updateFrustumPoints2(cam, points);
        WireFrustum wireFrustum = new WireFrustum(points);

        Geometry frustumGeo = new Geometry("Camera-Frustum", wireFrustum);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        frustumGeo.setMaterial(mat);
        frustumGeo.setCullHint(Spatial.CullHint.Never);
        frustumGeo.setShadowMode(RenderQueue.ShadowMode.Off);
        return frustumGeo;
    }
}
