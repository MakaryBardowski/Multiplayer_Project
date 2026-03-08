package com.mccurry.game.shared.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public abstract class MenuState extends BaseAppState implements MenuService {
    public static String METADATA_LOCALE_KEY = "LOCALE_KEY";

    private final Node stateGuiNode = new Node(
            "%s-$%s-stateGuiNode".formatted(this.getClass().getSimpleName(),System.identityHashCode(this))
    );

    private final Node stateSceneNode = new Node(
            "%s-$%s-stateSceneNode".formatted(this.getClass().getSimpleName(),System.identityHashCode(this))
    );


    private boolean initialized;

    @Override
    protected void initialize(Application application) {
        System.out.println("[%s] Attached.".formatted(this.getClass().getSimpleName()));
        if(!initialized) {
            var simpleApp = (SimpleApplication) application;
            createSceneOnce(simpleApp, stateSceneNode);
            createUiOnce(simpleApp, stateGuiNode);
            initialized = true;
        }
        var simpleApp = (SimpleApplication) application;
        simpleApp.getGuiNode().attachChild(stateGuiNode);
        simpleApp.getRootNode().attachChild(stateSceneNode);
    }

    @Override
    protected void cleanup(Application application) {
        stateGuiNode.removeFromParent();
        stateSceneNode.removeFromParent();
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}

    @Override
    public void createSceneOnce(SimpleApplication application, Node stateSceneNode) {}
}
