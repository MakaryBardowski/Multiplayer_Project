package com.mccurry.game.menu;

import com.jme3.app.Application;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.console.event.ConsoleRequestUiStateEvent;
import com.mccurry.game.console.event.MainThreadConsoleCallbackEvent;
import com.mccurry.game.gameplay.menu.pausemenu.PauseMenuState;
import com.mccurry.game.keyBinding.KeyMappingRegistry;
import com.mccurry.game.menu.gamemodeSelector.GamemodeSelectorState;
import com.mccurry.game.menu.lobby.singlePlayer.SinglePlayerLobbyState;
import com.mccurry.game.menu.main.MainMenuState;
import com.mccurry.game.menu.main.event.CloseMenuRequestEvent;
import com.mccurry.game.menu.main.event.OpenMenuRequestEvent;
import com.mccurry.game.menu.settings.UserSettingsMenuState;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.ui.MenuState;
import com.mccurry.game.shared.ui.MenuType;
import com.mccurry.game.shared.ui.event.MainThreadLocaleReloadedEvent;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeInEvent;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeOutEvent;
import com.mccurry.game.util.event.EventHandlers;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.mccurry.game.shared.ui.MenuType.*;

public class MenuManager implements EventSubscriber, ActionListener {
    private final Application application;
    private final SyncEventHandler<MainThreadLocaleReloadedEvent> localeChangeHandler;

    private final Map<MenuType, CachedMenu> menuCache = new HashMap<>();

    private final OverlayMenuManager overlayMenuManager;
    private final RootMenuManager rootMenuManager;

    private final SyncEventHandler<ConsoleRequestUiStateEvent> onMenuStateRequest;

    private MenuType currentRootMenuType = null;
    private boolean canPlayerCloseMenu;

    private final SyncEventHandler<StartScreenFadeInEvent> onStartScreenFadeIn = ((event, lifecycleManager) -> {
      canPlayerCloseMenu = false;
    });

    private final SyncEventHandler<StartScreenFadeOutEvent> onStartScreenFadeOut = ((event, lifecycleManager) -> {
        canPlayerCloseMenu = true;
    });

    private final SyncEventHandler<OpenMenuRequestEvent> onMenuOpenHandler = EventHandlers.syncEventHandler(
            (event, lcm) -> {
                openMenu(event.getMenuType());
            }
    );

    private final SyncEventHandler<CloseMenuRequestEvent> onMenuCloseHandler = EventHandlers.syncEventHandler(
            (event, lcm) -> {
                closeMenu(event.getMenuType());
            }
    );

    public MenuManager(Application application) {
        this.application = application;
        this.overlayMenuManager = new OverlayMenuManager(application);
        this.rootMenuManager = new RootMenuManager(application);


        var orchestrationBus = application.getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus();

        onMenuStateRequest = EventHandlers.syncEventHandler(
                (event, lcm) -> {

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
                            MainThreadConsoleCallbackEvent.builder()
                                    .message(uiStateBuilder.toString())
                                    .build(),
                            null
                    );
                }
        );


        var mainMenuState = new MainMenuState(orchestrationBus);
        localeChangeHandler = (
                event,
                lifecycleManager
        ) -> {
            mainMenuState.refreshLocale();
            rebuildMenus();
        };

        menuCache.put(
                MAIN_MENU,
                CachedMenu.builder()
                        .cachedInstance(mainMenuState)
                        .supplier(() -> null) // never used as MainMenu is never removed from cache
                        .build()
        );
        menuCache.put(
                SETTINGS,
                CachedMenu.builder()
                        .cachedInstance(null)
                        .supplier(() -> new UserSettingsMenuState())
                        .build()
        );

        menuCache.put(GAMEMODE_SELECTOR,
                CachedMenu.builder()
                        .cachedInstance(null)
                        .supplier(() -> new GamemodeSelectorState())
                        .build()
        );

        menuCache.put(LOBBY_SINGLEPLAYER,
                CachedMenu.builder()
                        .cachedInstance(null)
                        .supplier(() -> new SinglePlayerLobbyState())
                        .build()
        );

        menuCache.put(PAUSE_MENU,
                CachedMenu.builder()
                        .cachedInstance(null)
                        .supplier(()-> new PauseMenuState(orchestrationBus))
                        .build()
        );

        rebuildMenus();
    }

    private synchronized void closeMenu(MenuType menuType) {
        application.enqueue(() -> {
            var menu = menuCache.get(menuType).cachedInstance;

            if (menuType.getMenuLayer() == MenuLayer.OVERLAY) {
                overlayMenuManager.closeOverlay(menu);
            } else if (menuType.getMenuLayer() == MenuLayer.ROOT) {
                closeRootMenu(menu);
            }
        });
    }

    private synchronized void openMenu(MenuType menuType) {
        application.enqueue(() -> { // whole thing is enqueued so possibly creating a menu is on main thread
            var menu = menuCache.get(menuType).cachedInstance;

            var stateManager = application.getStateManager();
            if (stateManager.hasState(menu)) {
                return;
            }


            if (menuType.getMenuLayer() == MenuLayer.OVERLAY) {
                overlayMenuManager.openOverlay(menu);
            } else if (menuType.getMenuLayer() == MenuLayer.ROOT) {
                openRootMenu(menuType, menu);
            }
        });
    }

    private void openRootMenu(MenuType targetMenuType,MenuState menuState) {
        var currentRoot = rootMenuManager.getCurrentRootMenu();
        if (currentRoot == null || !currentRoot.equals(menuState)) { // if root is changed, clear stack for it
            overlayMenuManager.closeAllOverlays();
            rootMenuManager.closeCurrentRoot();
            rootMenuManager.openMenu(menuState);
            currentRootMenuType = targetMenuType;
        }
    }

    private void closeRootMenu(MenuState menuState) {
        var currentRoot = rootMenuManager.getCurrentRootMenu();
        if (currentRoot != null && currentRoot.equals(menuState)) {
            overlayMenuManager.closeAllOverlays();
            rootMenuManager.closeCurrentRoot();
            currentRootMenuType = null;
        }
    }

    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(OpenMenuRequestEvent.class, onMenuOpenHandler);
        eventBus.subscribe(CloseMenuRequestEvent.class, onMenuCloseHandler);
        eventBus.subscribe(MainThreadLocaleReloadedEvent.class, localeChangeHandler);
        eventBus.subscribe(ConsoleRequestUiStateEvent.class,onMenuStateRequest);

        eventBus.subscribe(StartScreenFadeInEvent.class, onStartScreenFadeIn);
        eventBus.subscribe(StartScreenFadeOutEvent.class, onStartScreenFadeOut);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(OpenMenuRequestEvent.class, onMenuOpenHandler);
        eventBus.unsubscribe(CloseMenuRequestEvent.class, onMenuCloseHandler);
        eventBus.unsubscribe(MainThreadLocaleReloadedEvent.class, localeChangeHandler);
        eventBus.unsubscribe(ConsoleRequestUiStateEvent.class,onMenuStateRequest);

        eventBus.unsubscribe(StartScreenFadeInEvent.class, onStartScreenFadeIn);
        eventBus.unsubscribe(StartScreenFadeOutEvent.class, onStartScreenFadeOut);
    }

    private void rebuildMenus() {
        for (var menuType : MenuType.values()) {
            if (!menuType.equals(MAIN_MENU)) {
                var cachedMenu = menuCache.get(menuType);

                if (cachedMenu.cachedInstance != null) { // just in case - this shouldnt ever trigger because locale can be changed from main menu only (and its never destroyed)
                    application.getStateManager().detach(cachedMenu.cachedInstance);
                }

                cachedMenu.setCachedInstance(cachedMenu.supplier.get());
            }
        }
    }

    public void startListening(InputManager inputManager){
        inputManager.addListener(this, KeyMappingRegistry.MAPPING_KEY_ESCAPE);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(canPlayerCloseMenu && name.equals(KeyMappingRegistry.MAPPING_KEY_ESCAPE) && !isPressed){
            if(overlayMenuManager.anyOverlaysActive()) {
                overlayMenuManager.closeTopOverlay();
                return;
            }


            if(rootMenuManager.getCurrentRootMenu() == null){
                openMenu(PAUSE_MENU);
                return;
            }

            if(currentRootMenuType.equals(MAIN_MENU)){
                return;
            }

            if(currentRootMenuType.equals(LOBBY_SINGLEPLAYER)){
                openMenu(GAMEMODE_SELECTOR);
                return;
            } else if(currentRootMenuType.equals(GAMEMODE_SELECTOR)){
                openMenu(MAIN_MENU);
                return;
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
