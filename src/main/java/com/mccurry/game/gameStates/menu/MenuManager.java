package com.mccurry.game.gameStates.menu;

import com.jme3.app.Application;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameStates.menu.main.MainMenuState;
import com.mccurry.game.gameStates.menu.main.event.MenuOpenRequest;
import com.mccurry.game.gameStates.menu.settings.UserSettingsMenuState;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.ui.MenuState;
import com.mccurry.game.shared.ui.MenuType;
import com.mccurry.game.shared.ui.event.MainThreadLocaleReloadedEvent;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.mccurry.game.shared.ui.MenuType.MAIN_MENU;
import static com.mccurry.game.shared.ui.MenuType.SETTINGS;

public class MenuManager implements SyncEventHandler<MenuOpenRequest,GameOrchestrationContext>, EventSubscriber<GameOrchestrationContext> {
    private final Application application;
    private final SyncEventHandler<MainThreadLocaleReloadedEvent,GameOrchestrationContext> localeChangeHandler;

    private final Map<MenuType, CachedMenu> MENU_CACHE = new HashMap<>();

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

        MENU_CACHE.put(
                MAIN_MENU,
                CachedMenu.builder()
                        .cachedInstance(mainMenuState)
                        .supplier(()-> null) // never used as MainMenu is never removed from cache
                .build()
        );
        MENU_CACHE.put(
                SETTINGS,
                CachedMenu.builder()
                        .cachedInstance(null)
                        .supplier(()-> new UserSettingsMenuState())
                        .build()
        );

        rebuildMenus();
    }

    private void openMenu(MenuType menuType){
        var menu = MENU_CACHE.get(menuType).cachedInstance;

        var stateManager = application.getStateManager();
        if(stateManager.hasState(menu)){ // we check just to not enqueue additional stuff
            return;
        }
        application.enqueue(()->{
            stateManager.attach(menu);
        });
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
                var cachedMenu = MENU_CACHE.get(menuType);
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
