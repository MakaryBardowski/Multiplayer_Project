package com.mccurry.game.orchestrator;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.boot.InitialBootState;
import com.mccurry.game.boot.events.AssetLoadingCompleteEvent;
import com.mccurry.game.boot.events.InitialBootCompleteEvent;
import com.mccurry.game.console.CheatConsoleLauncher;
import com.mccurry.game.console.event.AttachConsoleListenerEvent;
import com.mccurry.game.menu.OverlayMenuManager;
import com.mccurry.game.menu.MenuManager;
import com.mccurry.game.orchestrator.service.OnAssetLoadingDoneMainMenuLauncher;
import com.mccurry.game.keyBinding.KeyBindingService;
import com.mccurry.game.keyBinding.model.PlayerAction;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.settings.service.UserSettingsService;
import com.mccurry.game.shared.ui.transition.ScreenTransitionService;
import com.mccurry.game.util.event.EventSubscribers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GameStateOrchestrator extends BaseAppState {

    private final EventBus<GameOrchestrationContext> gameOrchestrationBus;

    @Getter(AccessLevel.NONE)
    private final UserSettingsService userSettingsService;

    private final KeyBindingService keyBindingService;

    @Override
    protected void initialize(Application application) {
        new ScreenTransitionService(application.getStateManager()).subscribe(gameOrchestrationBus);
        new MenuManager(application).subscribe(gameOrchestrationBus);

        var bootDoneHandler = new OnAssetLoadingDoneMainMenuLauncher(gameOrchestrationBus);
        gameOrchestrationBus.subscribe(AssetLoadingCompleteEvent.class, bootDoneHandler);

        attachConsoleListenerSubscriberOnce().subscribe(gameOrchestrationBus);
        var initialBootState = new InitialBootState();
        getStateManager().attach(initialBootState);

        closeInitialBootStateSubscriber(initialBootState).subscribe(gameOrchestrationBus); // todo: bug? co jesli asset loading skonczy sie przed zarejestrowaniem tego subscribera?

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

    private void registerConsoleLauncher(KeyBindingService keyBindingService){
       var launcher =  new CheatConsoleLauncher(getApplication(),keyBindingService.getKeybindForPlayerAction(PlayerAction.OPEN_CONSOLE).getKeyName());
       launcher.startListening();
       launcher.subscribe(gameOrchestrationBus);
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

    private EventSubscriber<GameOrchestrationContext> attachConsoleListenerSubscriberOnce(){
        return EventSubscribers.syncUnsubscribingOnFirstMessage(AttachConsoleListenerEvent.class,gameOrchestrationBus,(event)->{
            registerConsoleLauncher(keyBindingService);
        });
    }

    @Override
    public void update(float tpf) {}

    public UserSettings getUserSettingsDeepClone(){
        return userSettingsService.getCurrentSettingsDeepClone();
    }
}
