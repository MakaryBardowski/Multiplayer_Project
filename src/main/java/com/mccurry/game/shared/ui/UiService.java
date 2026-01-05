package com.mccurry.game.shared.ui;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;

public interface UiService {
    void createUi(SimpleApplication application, Node stateGuiNode, UiResolutionSettings uiResolutionSettings);
    void layoutUi(UiResolutionSettings uiResolutionSettings);
}
