package com.mccurry.game.shared.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;

public abstract class MenuState extends BaseAppState implements MenuService {
    public static String METADATA_LOCALE_KEY = "LOCALE_KEY";

    private final Node stateGuiNode = new Node(
            "%s-$%s-stateNode".formatted(this.getClass().getSimpleName(),System.identityHashCode(this))
    );

    private boolean initialized;

    @Override
    protected void initialize(Application application) {
        System.out.println("[%s] Attached.".formatted(this.getClass().getSimpleName()));
        if(!initialized) {
            createUi((SimpleApplication) application, stateGuiNode);
            initialized = true;
        }
        var simpleApp = (SimpleApplication) application;
        simpleApp.getGuiNode().attachChild(stateGuiNode);
    }

    @Override
    protected void cleanup(Application application) {
        stateGuiNode.removeFromParent();
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}
}
