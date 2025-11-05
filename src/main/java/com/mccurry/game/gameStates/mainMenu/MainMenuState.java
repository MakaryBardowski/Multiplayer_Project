package com.mccurry.game.gameStates.mainMenu;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.mccurry.game.gameStates.mainMenu.events.MainMenuOpened;
import com.mccurry.game.gameStates.orchestrator.GameStateEventContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;

public class MainMenuState extends BaseAppState {
    @Override
    protected void initialize(Application application) {
        System.out.println("[MainMenuState] Main menu opened");

        application.getViewPort().setBackgroundColor(ColorRGBA.Cyan);

        getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus().publish(
                new MainMenuOpened(),
                new GameStateEventContext(),
                null
        );
    }

    @Override
    protected void cleanup(Application application) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
