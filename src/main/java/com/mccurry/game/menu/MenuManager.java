package com.mccurry.game.menu;

import com.jme3.app.Application;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.console.event.ConsoleRequestUiStateEvent;
import com.mccurry.game.console.event.MainThreadConsoleStateResponseEvent;
import com.mccurry.game.menu.gamemodeSelector.GamemodeSelectorState;
import com.mccurry.game.menu.lobby.singlePlayer.SinglePlayerLobbyState;
import com.mccurry.game.menu.main.MainMenuState;
import com.mccurry.game.menu.main.event.CloseMenuRequestEvent;
import com.mccurry.game.menu.main.event.OpenMenuRequestEvent;
import com.mccurry.game.menu.settings.UserSettingsMenuState;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.ui.MenuState;
import com.mccurry.game.shared.ui.MenuType;
import com.mccurry.game.shared.ui.event.MainThreadLocaleReloadedEvent;
import com.mccurry.game.util.event.EventHandlers;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.mccurry.game.shared.ui.MenuType.*;

public class MenuManager implements EventSubscriber<GameOrchestrationContext> {
    private final Application application;
    private final SyncEventHandler<MainThreadLocaleReloadedEvent, GameOrchestrationContext> localeChangeHandler;

    private final Map<MenuType, CachedMenu> rootMenuCache = new HashMap<>();
    private final Map<MenuType, CachedMenu> overlayMenuCache = new HashMap<>();

    private final OverlayMenuManager overlayMenuManager;
    private final RootMenuManager rootMenuManager;

    private final SyncEventHandler<ConsoleRequestUiStateEvent, GameOrchestrationContext> onMenuStateRequest;


    private final SyncEventHandler<OpenMenuRequestEvent, GameOrchestrationContext> onMenuOpenHandler = EventHandlers.syncEventHandler(
            (event, ctx, lcm) -> {
                openMenu(event.getMenuType());
            }
    );

    private final SyncEventHandler<CloseMenuRequestEvent, GameOrchestrationContext> onMenuCloseHandler = EventHandlers.syncEventHandler(
            (event, ctx, lcm) -> {
                closeMenu(event.getMenuType());
            }
    );

    public MenuManager(Application application) {
        this.application = application;
        this.overlayMenuManager = new OverlayMenuManager(application);
        this.rootMenuManager = new RootMenuManager(application);


        var orchestrationBus = application.getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus();

        onMenuStateRequest = EventHandlers.syncEventHandler(
                (event, ctx, lcm) -> {

                    var root = rootMenuManager.getCurrentRootMenu();

                    var uiStateBuilder = new StringBuilder();
                    uiStateBuilder.append("+");
                    uiStateBuilder.append(root == null ? "null" : root.getClass().getSimpleName());

                    var iterator = overlayMenuManager.getOverlayStackClone().iterator();
                    while (iterator.hasNext()) {
                        var overlay = iterator.next();
                        uiStateBuilder.append("\n   +");
                        uiStateBuilder.append(overlay.getClass().getSimpleName());
                        if(!iterator.hasNext()){
                            uiStateBuilder.append(" [TOP]");
                        }
                    }


                    orchestrationBus.publish(
                            MainThreadConsoleStateResponseEvent.builder()
                                    .uiState(uiStateBuilder.toString())
                                    .build(),
                            new GameOrchestrationContext(),
                            null
                    );
                }
        );


        var mainMenuState = new MainMenuState(orchestrationBus);
        localeChangeHandler = (
                event,
                eventContext,
                lifecycleManager
        ) -> {
            mainMenuState.refreshLocale();
            rebuildMenus();
        };

        rootMenuCache.put(
                MAIN_MENU,
                CachedMenu.builder()
                        .cachedInstance(mainMenuState)
                        .supplier(() -> null) // never used as MainMenu is never removed from cache
                        .build()
        );
        rootMenuCache.put(
                SETTINGS,
                CachedMenu.builder()
                        .cachedInstance(null)
                        .supplier(() -> new UserSettingsMenuState())
                        .build()
        );

        rootMenuCache.put(GAMEMODE_SELECTOR,
                CachedMenu.builder()
                        .cachedInstance(null)
                        .supplier(() -> new GamemodeSelectorState())
                        .build()
        );

        rootMenuCache.put(LOBBY_SINGLEPLAYER,
                CachedMenu.builder()
                        .cachedInstance(null)
                        .supplier(() -> new SinglePlayerLobbyState())
                        .build()
        );

        rebuildMenus();
    }

    private synchronized void closeMenu(MenuType menuType) {
        application.enqueue(() -> {
            var menu = rootMenuCache.get(menuType).cachedInstance;

            if (menuType.getMenuLayer() == MenuLayer.OVERLAY) {
                overlayMenuManager.closeOverlay(menu);
            } else if (menuType.getMenuLayer() == MenuLayer.ROOT) {
                closeRootMenu(menu);
            }
        });
    }

    private synchronized void openMenu(MenuType menuType) {
        application.enqueue(() -> { // whole thing is enqueued so possibly creating a menu is on main thread
            var menu = rootMenuCache.get(menuType).cachedInstance;

            var stateManager = application.getStateManager();
            if (stateManager.hasState(menu)) {
                return;
            }


            if (menuType.getMenuLayer() == MenuLayer.OVERLAY) {
                overlayMenuManager.openOverlay(menu);
            } else if (menuType.getMenuLayer() == MenuLayer.ROOT) {
                openRootMenu(menu);
            }
        });
    }

    private void openRootMenu(MenuState menuState) {
        var currentRoot = rootMenuManager.getCurrentRootMenu();
        if (currentRoot == null || !currentRoot.equals(menuState)) { // if root is changed, clear stack for it
            overlayMenuManager.closeAllOverlays();
            rootMenuManager.closeCurrentRoot();
            rootMenuManager.openMenu(menuState);
        }
    }

    private void closeRootMenu(MenuState menuState) {
        var currentRoot = rootMenuManager.getCurrentRootMenu();
        if (currentRoot != null && currentRoot.equals(menuState)) {
            overlayMenuManager.closeAllOverlays();
            rootMenuManager.closeCurrentRoot();
        }
    }

    @Override
    public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(OpenMenuRequestEvent.class, onMenuOpenHandler);
        eventBus.subscribe(CloseMenuRequestEvent.class, onMenuCloseHandler);
        eventBus.subscribe(MainThreadLocaleReloadedEvent.class, localeChangeHandler);
        eventBus.subscribe(ConsoleRequestUiStateEvent.class,onMenuStateRequest);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(OpenMenuRequestEvent.class, onMenuOpenHandler);
        eventBus.unsubscribe(CloseMenuRequestEvent.class, onMenuCloseHandler);
        eventBus.unsubscribe(MainThreadLocaleReloadedEvent.class, localeChangeHandler);
        eventBus.unsubscribe(ConsoleRequestUiStateEvent.class,onMenuStateRequest);
    }

    private void rebuildMenus() {
        for (var menuType : MenuType.values()) {
            if (!menuType.equals(MAIN_MENU)) {
                var cachedMenu = rootMenuCache.get(menuType);

                if (cachedMenu.cachedInstance != null) { // just in case - this shouldnt ever trigger because locale can be changed from main menu only (and its never destroyed)
                    application.getStateManager().detach(cachedMenu.cachedInstance);
                }

                cachedMenu.setCachedInstance(cachedMenu.supplier.get());
            }
        }
    }


    @Setter
    @Builder
    private static class CachedMenu {
        @Nullable
        private MenuState cachedInstance;
        @NonNull
        private Supplier<MenuState> supplier;
    }
}
