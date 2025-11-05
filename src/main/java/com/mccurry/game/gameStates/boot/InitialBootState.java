package com.mccurry.game.gameStates.boot;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameStates.boot.assetLoader.AssetLoader;
import com.mccurry.game.gameStates.boot.events.BootUiStateSetupCompleteEvent;
import com.mccurry.game.gameStates.boot.events.InitialBootCompleteEvent;
import com.mccurry.game.gameStates.boot.ui.LoadingScreenState;
import com.mccurry.game.gameStates.mainMenu.events.MainMenuOpened;
import com.mccurry.game.gameStates.orchestrator.GameStateEventContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeInEvent;
import lombok.NonNull;

public class InitialBootState extends BaseAppState {

    @Override
    protected void initialize(Application application) {
        var stateManager = getStateManager();
        var orchestrationBus = stateManager.getState(GameStateOrchestrator.class).getGameOrchestrationBus();

        var assetLoader = new AssetLoader(orchestrationBus);
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

    private EventSubscriber<GameStateEventContext> assetLoadingFinishedSubscriber(EventBus<GameStateEventContext> orchestrationBus) {
        return new EventSubscriber<>() {
            private final SyncEventHandler<MainMenuOpened, GameStateEventContext> handler = (event, ctx, lifecycleManager) -> {
                orchestrationBus.publish(
                        new StartScreenFadeInEvent(
                                4f,
                                () -> {
                                    orchestrationBus.publish(new InitialBootCompleteEvent(), new GameStateEventContext(), null);
                                }
                        ),
                        new GameStateEventContext(),
                        null
                );
                unsubscribe(orchestrationBus);
            };

            @Override
            public void subscribe(@NonNull EventBus<GameStateEventContext> eventBus) {
                eventBus.subscribe(MainMenuOpened.class, handler);
            }

            @Override
            public void unsubscribe(@NonNull EventBus<GameStateEventContext> eventBus) {
                eventBus.unsubscribe(MainMenuOpened.class, handler);
            }
        };
    }
}
