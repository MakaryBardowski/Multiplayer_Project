package com.mccurry.game.shared.ui.transition;

import com.jme3.app.state.AppStateManager;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameStates.orchestrator.GameStateEventContext;
import com.mccurry.game.shared.ui.transition.appStates.AutoDetachingScreenFadeInState;
import com.mccurry.game.shared.ui.transition.appStates.AutoDetachingScreenFadeOutState;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeInEvent;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeOutEvent;
import lombok.NonNull;

public final class ScreenTransitionService implements EventSubscriber<GameStateEventContext> {
    private final SyncEventHandler<StartScreenFadeInEvent, GameStateEventContext> fadeInEventHandler;
    private final SyncEventHandler<StartScreenFadeOutEvent, GameStateEventContext> fadeOutEventHandler;

    public ScreenTransitionService(AppStateManager stateManager) {
        fadeInEventHandler = (event, eventContext, lifecycleManager) -> {
            stateManager.attach(
                    new AutoDetachingScreenFadeInState(
                            event.getFadeInTime(),
                            event.getOnFadeInFinished()
                    )
            );
        };
        fadeOutEventHandler = (event, eventContext, lifecycleManager) -> {
            stateManager.attach(new AutoDetachingScreenFadeOutState(event.getFadeOutTime()));
        };
    }

    @Override
    public void subscribe(@NonNull EventBus<GameStateEventContext> eventBus) {
        eventBus.subscribe(StartScreenFadeInEvent.class, fadeInEventHandler);
        eventBus.subscribe(StartScreenFadeOutEvent.class, fadeOutEventHandler);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameStateEventContext> eventBus) {
        eventBus.unsubscribe(StartScreenFadeInEvent.class, fadeInEventHandler);
        eventBus.unsubscribe(StartScreenFadeOutEvent.class, fadeOutEventHandler);
    }
}
