package com.mccurry.game;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.constants.CameraConstants;
import com.mccurry.game.keyBinding.KeyMappingService;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.locale.LocaleService;
import com.mccurry.game.shared.settings.elements.option.keybind.KeyRebindInputListener;
import com.mccurry.game.shared.settings.service.UserSettingsService;
import com.mccurry.game.shared.ui.event.MainThreadResizeUiEvent;
import com.mccurry.game.sound.SoundRegistry;
import com.mccurry.game.sound.SoundService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Main extends SimpleApplication {
    private final EventBus orchestrationBus;

    @Setter
    private UserSettingsService userSettingsService;

    @Setter
    private KeyMappingService keyMappingService;

    @Setter
    private LocaleService localeService;

    @Setter
    private SoundService soundService;

    private final SyncEventHandler<MainThreadResizeUiEvent> resizeUiEventHandler = resizeUiOnResolutionChanged();

    @Getter
    private static int guiScale;

    private final Node gameGuiNode = new Node("Main gui node");
    private final Node rootNonPositionalSoundNode = new Node("root non-positional sound node");

    @Override
    public void simpleInitApp() {
        stateManager.detach(
                stateManager.getState(FlyCamAppState.class)
        );
        clearJmeMappings();

        CameraConstants.applyDefaultTransform(getCamera());

        this.guiNode.attachChild(gameGuiNode);
        viewPort.setBackgroundColor(ColorRGBA.Black);
        stateManager.attach(new ScreenshotAppState());
        System.out.println("\n\n\n\n[Main] Starting");

        keyMappingService.initializeMappings(inputManager);

        var currentSettings = userSettingsService.getCurrentSettingsDeepClone();
        var soundRegistry = SoundRegistry.withSoundVolumesFromSettings(assetManager,currentSettings);
        soundRegistry.subscribe(orchestrationBus);

        var soundService = new SoundService(
                Thread.currentThread(),
                this,rootNonPositionalSoundNode,
                soundRegistry
        );
        this.setSoundService(soundService);
        this.soundService.subscribe(orchestrationBus);



        stateManager.attach(new GameStateOrchestrator(orchestrationBus, userSettingsService, keyMappingService));
    }

    public static void main(String[] args) {
        var orchestrationBus = new EventBus();


        Main app = new Main(orchestrationBus);
        KeyRebindInputListener.initialize(app);

        orchestrationBus.subscribe(MainThreadResizeUiEvent.class, app.resizeUiEventHandler);

        var userSettingsService = new UserSettingsService(orchestrationBus, app);
        userSettingsService.subscribe(orchestrationBus);

        app.setUserSettingsService(userSettingsService);
        var settings = userSettingsService.getApplicationSettings();
        app.setSettings(settings);


        var userSettings = userSettingsService.getCurrentSettingsDeepClone(); // same settings, just different class
        app.resizeUiEventHandler.handle(
                new MainThreadResizeUiEvent(userSettings.getResolutionSetting().getUiScale()),
                null
        );

        app.setKeyMappingService(KeyMappingService.withKeybindsFromSettings(orchestrationBus,userSettings));
        app.setLocaleService(LocaleService.withLocaleFromSettings(orchestrationBus,userSettings));

        app.start();
    }

    private SyncEventHandler<MainThreadResizeUiEvent> resizeUiOnResolutionChanged() {
        return (event, lifecycleManager)
                -> {
            guiScale = event.getUiScale();
            gameGuiNode.setLocalScale(guiScale, guiScale, 1);
        };
    }

    @Override
    public Node getGuiNode(){
        return gameGuiNode;
    }

    private void clearJmeMappings(){
        inputManager.deleteMapping(INPUT_MAPPING_EXIT);
        inputManager.deleteMapping(INPUT_MAPPING_HIDE_STATS);
    }
}
