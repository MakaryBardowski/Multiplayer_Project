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
            hideTopOverlay();
        }

        overlayStack.push(menu);
        stateManager.attach(menu);
    }

    public boolean anyOverlaysActive(){
        return !overlayStack.isEmpty();
    }

    private void hideTopOverlay() {
        var currentOverlay = overlayStack.peek();
        currentOverlay.setEnabled(false);
    }

    private void showTopOverlay(){
        var currentOverlay = overlayStack.peek();
        currentOverlay.setEnabled(true);
    }

    public void closeTopOverlay() {
        var previousOverlay = overlayStack.pop();
        stateManager.detach(previousOverlay);


        if (!overlayStack.isEmpty()) {
            showTopOverlay();
        }
    }

    public void closeOverlay(@NonNull MenuState overlay) {
        if(overlayStack.isEmpty()){
            return;
        }

        var topOverlay = overlayStack.peek();

        if(topOverlay != overlay){
            overlayStack.remove(overlay);
            stateManager.detach(overlay);
            return;
        }

        closeTopOverlay();
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
