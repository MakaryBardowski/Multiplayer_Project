package com.mccurry.game.shared.ui;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;

public interface UiService {
    void createUi(SimpleApplication application, Node stateGuiNode);
}
