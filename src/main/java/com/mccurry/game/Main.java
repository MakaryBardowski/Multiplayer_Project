package com.mccurry.game;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.settings.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Main extends SimpleApplication {
    private final EventBus<GameOrchestrationContext> orchestrationBus;

    @Setter
    private UserSettingsService userSettingsService;

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.Black);
        System.out.println("\n\n\n\n[Main] Starting");
        stateManager.attach(new GameStateOrchestrator(orchestrationBus,userSettingsService));
    }

    public static void main(String[] args) {
        var orchestrationBus = new EventBus<GameOrchestrationContext>();
        Main app = new Main(orchestrationBus);
        var userSettingsService = new UserSettingsService(orchestrationBus, app);
        userSettingsService.subscribe(orchestrationBus);

        app.setUserSettingsService(userSettingsService);

        app.setSettings(userSettingsService.getApplicationSettings());
        app.start();
    }
}
