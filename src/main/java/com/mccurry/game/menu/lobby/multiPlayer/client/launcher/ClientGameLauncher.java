package com.mccurry.game.menu.lobby.multiPlayer.client.launcher;


import com.jme3.app.state.AppStateManager;
import com.mccurry.game.gameplay.gamestate.GameStateFactory;
import com.mccurry.game.menu.main.event.CloseMenuRequestEvent;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.ui.MenuType;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeInEvent;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeOutEvent;

public class ClientGameLauncher {

    public void launch(AppStateManager appStateManager, ClientGameLaunchParams launchParams) {
        var orchestrationBus = appStateManager.getState(GameStateOrchestrator.class).getGameOrchestrationBus();

        orchestrationBus.publish(
                new StartScreenFadeInEvent(1, () -> {


                    orchestrationBus.publish(
                            CloseMenuRequestEvent.builder()
                                    .menuType(MenuType.LOBBY_HOST)
                                    .build(),
                            null
                    );


                    var gameState = GameStateFactory.client(launchParams.getConnectedClient());
                    appStateManager.attach(gameState);

                    orchestrationBus.publish(new StartScreenFadeOutEvent(1), null);
                }
                ),
                null
        );


    }
}
