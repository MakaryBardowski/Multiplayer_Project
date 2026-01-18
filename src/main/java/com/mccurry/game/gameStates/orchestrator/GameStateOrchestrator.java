package com.mccurry.game.gameStates.orchestrator;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameStates.boot.InitialBootState;
import com.mccurry.game.gameStates.boot.events.AssetLoadingCompleteEvent;
import com.mccurry.game.gameStates.boot.events.InitialBootCompleteEvent;
import com.mccurry.game.gameStates.keyBinding.KeyBindingService;
import com.mccurry.game.gameStates.keyBinding.model.KeyBind;
import com.mccurry.game.gameStates.keyBinding.model.PlayerAction;
import com.mccurry.game.gameStates.menu.main.MainMenuState;
import com.mccurry.game.gameStates.menu.main.event.MainMenuOpenRequest;
import com.mccurry.game.gameStates.orchestrator.service.OnAssetLoadingDoneMainMenuLauncher;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.settings.service.UserSettingsService;
import com.mccurry.game.shared.ui.transition.ScreenTransitionService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public class GameStateOrchestrator extends BaseAppState {

    private final EventBus<GameOrchestrationContext> gameOrchestrationBus;

    @Getter(AccessLevel.NONE)
    private final UserSettingsService userSettingsService;

    @Getter
    private final KeyBindingService keyBindingService;

    @Override
    protected void initialize(Application application) {
        new ScreenTransitionService(application.getStateManager()).subscribe(gameOrchestrationBus);
        registerMainMenuLauncher();

        var bootDoneHandler = new OnAssetLoadingDoneMainMenuLauncher(gameOrchestrationBus);
        gameOrchestrationBus.subscribe(AssetLoadingCompleteEvent.class, bootDoneHandler);

        var initialBootState = new InitialBootState();
        getStateManager().attach(initialBootState);

        closeInitialBootStateSubscriber(initialBootState).subscribe(gameOrchestrationBus);
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

    private EventSubscriber<GameOrchestrationContext> closeInitialBootStateSubscriber(InitialBootState initialBootState) {
        return new EventSubscriber<>() {
            private final SyncEventHandler<InitialBootCompleteEvent, GameOrchestrationContext> handler = (event, ctx, lifecycleManager) -> {
                getStateManager().detach(initialBootState);
                unsubscribe(gameOrchestrationBus);
                System.out.println("[GameStateOrchestrator] Initial boot complete. Detaching initial boot state");
            };

            @Override
            public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
                eventBus.subscribe(InitialBootCompleteEvent.class, handler);
            }

            @Override
            public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
                eventBus.unsubscribe(InitialBootCompleteEvent.class, handler);
            }
        };
    }

    @Override
    public void update(float tpf) {}

    private void registerMainMenuLauncher() {
        var launcherHandler = new SyncEventHandler<MainMenuOpenRequest, GameOrchestrationContext>() {
            private MainMenuState mainMenuState;

            @Override
            public void handle(@NonNull MainMenuOpenRequest event, @NonNull GameOrchestrationContext eventContext, @Nullable EventLifecycleManager<MainMenuOpenRequest> lifecycleManager) {
                if (mainMenuState == null) {
                    mainMenuState = MainMenuState.getNew();
                }
                var stateManager = getStateManager();
                if(stateManager.hasState(mainMenuState)){ // we check just to not enqueue additional stuff
                    return;
                }
                getApplication().enqueue(()->{
                    stateManager.attach(mainMenuState);
                });
            }
        };

        var launcher = new EventSubscriber<GameOrchestrationContext>() {
            @Override
            public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
                eventBus.subscribe(MainMenuOpenRequest.class, launcherHandler);
            }

            @Override
            public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
                eventBus.unsubscribe(MainMenuOpenRequest.class, launcherHandler);
            }
        };

        launcher.subscribe(gameOrchestrationBus);
    }

    public UserSettings getUserSettings(){
        return userSettingsService.getCurrentSettings();
    }
}
