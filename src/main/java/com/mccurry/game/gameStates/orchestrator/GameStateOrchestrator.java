package com.mccurry.game.gameStates.orchestrator;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameStates.boot.InitialBootState;
import com.mccurry.game.gameStates.boot.events.AssetLoadingCompleteEvent;
import com.mccurry.game.gameStates.boot.events.InitialBootCompleteEvent;
import com.mccurry.game.gameStates.orchestrator.service.MainMenuLauncher;
import com.mccurry.game.shared.ui.transition.ScreenTransitionService;
import lombok.Getter;
import lombok.NonNull;

public class GameStateOrchestrator extends BaseAppState {

    @Getter
    private final EventBus<GameStateEventContext> gameOrchestrationBus = new EventBus<>();

    @Override
    protected void initialize(Application application) {
        new ScreenTransitionService(application.getStateManager()).subscribe(gameOrchestrationBus);
        var bootDoneHandler = new MainMenuLauncher(getStateManager(), gameOrchestrationBus);
        gameOrchestrationBus.subscribe(AssetLoadingCompleteEvent.class, bootDoneHandler);

        var initialBootState = new InitialBootState();
        getStateManager().attach(initialBootState);

        closeInitialBootStateSubscriber(initialBootState).subscribe(gameOrchestrationBus);
    }

    @Override
    protected void cleanup(Application application) {}

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}

    private EventSubscriber<GameStateEventContext> closeInitialBootStateSubscriber(InitialBootState initialBootState){
        return new EventSubscriber<>() {
            private final SyncEventHandler<InitialBootCompleteEvent,GameStateEventContext> handler = (event, ctx, lifecycleManager) -> {
                getStateManager().detach(initialBootState);
                unsubscribe(gameOrchestrationBus);
                System.out.println("[GameStateOrchestrator] Initial boot complete. Detaching initial boot state");
            };

            @Override
            public void subscribe(@NonNull EventBus<GameStateEventContext> eventBus) {
                eventBus.subscribe(InitialBootCompleteEvent.class, handler);
            }

            @Override
            public void unsubscribe(@NonNull EventBus<GameStateEventContext> eventBus) {
                eventBus.unsubscribe(InitialBootCompleteEvent.class, handler);
            }
        };
    }
}
