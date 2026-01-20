package com.mccurry.game;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameStates.keyBinding.KeyBindingService;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.settings.elements.option.keybind.KeybindInputListener;
import com.mccurry.game.shared.settings.service.UserSettingsService;
import com.mccurry.game.shared.ui.event.MainThreadResizeUiEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Main extends SimpleApplication {
    private final EventBus<GameOrchestrationContext> orchestrationBus;

    @Setter
    private UserSettingsService userSettingsService;

    @Setter
    private KeyBindingService keyBindingService;

    private final SyncEventHandler<MainThreadResizeUiEvent, GameOrchestrationContext> resizeUiEventHandler = resizeUiOnResolutionChanged();

    @Getter
    private static int guiScale;

    private final Node gameGuiNode = new Node("Main gui node");

    @Override
    public void simpleInitApp() {
        this.guiNode.attachChild(gameGuiNode);
        viewPort.setBackgroundColor(ColorRGBA.Black);
        stateManager.attach(new ScreenshotAppState());
        System.out.println("\n\n\n\n[Main] Starting");
        keyBindingService.initializeMappings(inputManager);
        stateManager.attach(new GameStateOrchestrator(orchestrationBus, userSettingsService, keyBindingService));
    }

    public static void main(String[] args) {
        var orchestrationBus = new EventBus<GameOrchestrationContext>();

        Main app = new Main(orchestrationBus);
        KeybindInputListener.initialize(app);

        orchestrationBus.subscribe(MainThreadResizeUiEvent.class, app.resizeUiEventHandler);

        var userSettingsService = new UserSettingsService(orchestrationBus, app);
        userSettingsService.subscribe(orchestrationBus);

        app.setUserSettingsService(userSettingsService);
        var settings = userSettingsService.getApplicationSettings();
        app.setSettings(settings);


        var userSettings = userSettingsService.getCurrentSettings(); // same settings, just different class
        app.resizeUiEventHandler.handle(
                new MainThreadResizeUiEvent(userSettings.getResolutionSetting().getUiScale()),
                new GameOrchestrationContext(),
                null
        );

        app.setKeyBindingService(KeyBindingService.withKeybindsFromSettings(userSettings));

        app.start();
    }

    private SyncEventHandler<MainThreadResizeUiEvent, GameOrchestrationContext> resizeUiOnResolutionChanged() {
        return (event, eventContext, lifecycleManager)
                -> {
            guiScale = event.getUiScale();
            gameGuiNode.setLocalScale(guiScale, guiScale, 1);
        };
    }

    @Override
    public Node getGuiNode(){
        return gameGuiNode;
    }
}
