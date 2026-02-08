package com.mccurry.game.menu;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.menu.gamemodeSelector.GamemodeSelectorState;
import com.mccurry.game.menu.main.MainMenuState;
import com.mccurry.game.menu.main.event.MenuOpenRequest;
import com.mccurry.game.menu.settings.UserSettingsMenuState;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.ui.MenuState;
import com.mccurry.game.shared.ui.MenuType;
import com.mccurry.game.shared.ui.event.MainThreadLocaleReloadedEvent;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

import static com.mccurry.game.shared.ui.MenuType.*;

public class MenuManager implements SyncEventHandler<MenuOpenRequest,GameOrchestrationContext>, EventSubscriber<GameOrchestrationContext> {
    private final Application application;
    private final SyncEventHandler<MainThreadLocaleReloadedEvent,GameOrchestrationContext> localeChangeHandler;

    private final Map<MenuType, CachedMenu> menuCache = new HashMap<>();
    private final Stack<MenuState> overlayStack = new Stack<>();

    public MenuManager(Application application){
        this.application = application;

        var mainMenuState = new MainMenuState(application.getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus());
        localeChangeHandler = (
                event,
                eventContext,
                lifecycleManager
        ) -> {
            mainMenuState.refreshLocale();
            rebuildMenus();
        };

        menuCache.put(
                MAIN_MENU,
                CachedMenu.builder()
                        .cachedInstance(mainMenuState)
                        .supplier(()-> null) // never used as MainMenu is never removed from cache
                .build()
        );
        menuCache.put(
                SETTINGS,
                CachedMenu.builder()
                        .cachedInstance(null)
                        .supplier(()-> new UserSettingsMenuState())
                        .build()
        );

        menuCache.put(GAMEMODE_SELECTOR,
                CachedMenu.builder()
                        .cachedInstance(null)
                        .supplier(()-> new GamemodeSelectorState())
                        .build()
        );

        rebuildMenus();
    }

    private void openMenu(MenuType menuType){
        var menu = menuCache.get(menuType).cachedInstance;

        var stateManager = application.getStateManager();
        if(stateManager.hasState(menu)){ // we check just to not enqueue additional stuff
            return;
        }

        if(menuType.getMenuLayer() == MenuLayer.OVERLAY){
            openOverlay(menu,stateManager);
        } else if (menuType.getMenuLayer() == MenuLayer.ROOT){
            application.enqueue(()->{
                stateManager.attach(menu);
            });
        }
    }

    private void openOverlay(MenuState menu, AppStateManager stateManager){
        application.enqueue(()->{
            if(!overlayStack.isEmpty()){
                hideCurrentOverlay(stateManager);
            }

            overlayStack.push(menu);
            stateManager.attach(menu);
        });
    }

    private void hideCurrentOverlay(AppStateManager stateManager){
        var previousOverlay = overlayStack.peek();
        stateManager.detach(previousOverlay);
    }

    private void closeCurrentOverlay(AppStateManager stateManager){
        var overlay = overlayStack.pop();
        stateManager.detach(overlay);
    }

    @Override
    public void handle(@NonNull MenuOpenRequest event, @NonNull GameOrchestrationContext eventContext, @Nullable EventLifecycleManager<MenuOpenRequest> lifecycleManager) {
        openMenu(event.getMenuType());
    }

    @Override
    public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(MenuOpenRequest.class,this);
        eventBus.subscribe(MainThreadLocaleReloadedEvent.class, localeChangeHandler);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(MenuOpenRequest.class,this);
        eventBus.unsubscribe(MainThreadLocaleReloadedEvent.class, localeChangeHandler);
    }

    private void rebuildMenus(){
        for(var menuType : MenuType.values()){
            if(!menuType.equals(MAIN_MENU)){
                var cachedMenu = menuCache.get(menuType);

                if(cachedMenu.cachedInstance != null){ // just in case - this shouldnt ever trigger because locale can be changed from main menu only (and its never destroyed)
                    application.getStateManager().detach(cachedMenu.cachedInstance);
                }

                cachedMenu.setCachedInstance(cachedMenu.supplier.get());
            }
        }
    }

    @Setter
    @Builder
    private static class CachedMenu{
        @Nullable private MenuState cachedInstance;
        @NonNull private Supplier<MenuState> supplier;
    }
}
