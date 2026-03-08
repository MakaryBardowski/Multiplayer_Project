package com.mccurry.game.menu;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.mccurry.game.shared.ui.MenuState;
import lombok.Getter;


public class RootMenuManager {

    @Getter
    private MenuState currentRootMenu;
    private final AppStateManager stateManager;

    public RootMenuManager(Application application){
        stateManager = application.getStateManager();
    }

    public void openMenu(MenuState menuState){
        stateManager.attach(menuState);
        currentRootMenu = menuState;
    }

    public void closeCurrentRoot(){
        if(currentRootMenu != null){
            stateManager.detach(currentRootMenu);
        }
        currentRootMenu = null;
    }
}
