package com.mccurry.game.menu;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.mccurry.game.shared.ui.MenuState;
import lombok.NonNull;

import java.util.Stack;

public class OverlayMenuManager {

    private final AppStateManager stateManager;
    private final Stack<MenuState> overlayStack = new Stack<>();

    public OverlayMenuManager(Application application) {
        this.stateManager = application.getStateManager();
    }

    public void openOverlay(MenuState menu) {
        if (!overlayStack.isEmpty()) {
            hideCurrentOverlay();
        }

        overlayStack.push(menu);
        stateManager.attach(menu);
    }

    private void hideCurrentOverlay() {
        var previousOverlay = overlayStack.peek();
        stateManager.detach(previousOverlay);
    }

    public void closeOverlay(@NonNull MenuState overlay) {
        overlayStack.remove(overlay);
        stateManager.detach(overlay);
    }

    public void closeAllOverlays(){
        while(!overlayStack.isEmpty()){
            var overlay = overlayStack.pop();
            stateManager.detach(overlay);
        }
    }

    public Stack<MenuState> getOverlayStackClone(){
        return (Stack<MenuState>) overlayStack.clone();
    }
}
