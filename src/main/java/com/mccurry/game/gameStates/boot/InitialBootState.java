package com.mccurry.game.gameStates.boot;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.game.gameStates.boot.assetLoader.AssetLoader;
import com.mccurry.game.gameStates.boot.events.BootUiStateSetupCompleteEvent;
import com.mccurry.game.gameStates.boot.events.InitialBootCompleteEvent;
import com.mccurry.game.gameStates.boot.ui.LoadingScreenState;
import com.mccurry.game.gameStates.mainMenu.events.MainMenuOpened;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeInEvent;
import com.mccurry.game.shared.util.event.EventSubscribers;

public class InitialBootState extends BaseAppState {

    @Override
    protected void initialize(Application application) {
        var stateManager = getStateManager();
        var orchestrationBus = stateManager.getState(GameStateOrchestrator.class).getGameOrchestrationBus();

        var assetLoader = new AssetLoader(orchestrationBus, (SimpleApplication) application);
        orchestrationBus.subscribe(BootUiStateSetupCompleteEvent.class, assetLoader);

        stateManager.attach(new LoadingScreenState());
        assetLoadingFinishedSubscriber(orchestrationBus).subscribe(orchestrationBus);
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

    private EventSubscriber<GameOrchestrationContext> assetLoadingFinishedSubscriber(EventBus<GameOrchestrationContext> orchestrationBus) {
        return EventSubscribers.syncUnsubscribingOnFirstMessage(
                MainMenuOpened.class,
                orchestrationBus,
                event -> orchestrationBus.publish(
                        new StartScreenFadeInEvent(
                                2.5f,
                                () -> {
                                    orchestrationBus.publish(new InitialBootCompleteEvent(), new GameOrchestrationContext(), null);
                                }
                        ),
                        new GameOrchestrationContext(),
                        null
                )
        );
    }
}
