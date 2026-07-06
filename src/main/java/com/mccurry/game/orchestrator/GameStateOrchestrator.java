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
import com.mccurry.game.menu.MenuManager;
import com.mccurry.game.orchestrator.service.OnAssetLoadingDoneMainMenuLauncher;
import com.mccurry.game.keyBinding.KeyMappingService;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.settings.service.UserSettingsService;
import com.mccurry.game.shared.ui.transition.ScreenTransitionService;
import com.mccurry.game.util.event.EventSubscribers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public class GameStateOrchestrator extends BaseAppState {

    private final EventBus gameOrchestrationBus;

    @Getter(AccessLevel.NONE)
    private final UserSettingsService userSettingsService;

    private final KeyMappingService keyMappingService;

    @Nullable
    private CheatConsoleLauncher cheatConsoleLauncher;

    @Override
    protected void initialize(Application application) {
        new ScreenTransitionService(application.getStateManager()).subscribe(gameOrchestrationBus);
        var menuManager = new MenuManager(application);
        menuManager.subscribe(gameOrchestrationBus);
        menuManager.startListening(getApplication().getInputManager());

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

    private void registerConsoleLauncher(){
       cheatConsoleLauncher =  new CheatConsoleLauncher(getApplication());
       cheatConsoleLauncher.startListening();
    }

    private EventSubscriber closeInitialBootStateSubscriber(InitialBootState initialBootState) {
        return new EventSubscriber() {
            private final SyncEventHandler<InitialBootCompleteEvent> handler = (event, lifecycleManager) -> {
                getStateManager().detach(initialBootState);
                unsubscribe(gameOrchestrationBus);
                System.out.println("[GameStateOrchestrator] Initial boot complete. Detaching initial boot state");
            };

            @Override
            public void subscribe(@NonNull EventBus eventBus) {
                eventBus.subscribe(InitialBootCompleteEvent.class, handler);
            }

            @Override
            public void unsubscribe(@NonNull EventBus eventBus) {
                eventBus.unsubscribe(InitialBootCompleteEvent.class, handler);
            }
        };
    }

    private EventSubscriber attachConsoleListenerSubscriberOnce(){
        return EventSubscribers.syncUnsubscribingOnFirstMessage(AttachConsoleListenerEvent.class,gameOrchestrationBus,(event)->{
            registerConsoleLauncher();
        });
    }

    @Override
    public void update(float tpf) {}

    public UserSettings getUserSettingsDeepClone(){
        return userSettingsService.getCurrentSettingsDeepClone();
    }
}
