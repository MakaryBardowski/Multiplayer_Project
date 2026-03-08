package com.mccurry.game.shared.ui;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;

public interface MenuService {
    void createUiOnce(SimpleApplication application, Node stateGuiNode);
    void createSceneOnce(SimpleApplication application, Node stateSceneNode);
}
