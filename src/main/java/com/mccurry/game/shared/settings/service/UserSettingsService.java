package com.mccurry.game.shared.settings.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.EventHandler;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.shared.settings.event.MainThreadResolutionChangedEvent;
import com.mccurry.game.shared.settings.exception.UserSettingsException;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.event.MainThreadRefreshUiEvent;
import com.simsilica.lemur.GuiGlobals;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;

public class UserSettingsService implements EventSubscriber<GameOrchestrationContext> {
    private static final ObjectMapper USER_SETTINGS_FILE_OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final EventHandler<MainThreadResolutionChangedEvent, GameOrchestrationContext> onResolutionChanged;

    @Getter
    private UserSettings currentSettings;

    public UserSettingsService(EventBus<GameOrchestrationContext> orchestrationBus, SimpleApplication application) {
        onResolutionChanged = (SyncEventHandler<MainThreadResolutionChangedEvent, GameOrchestrationContext>) (event, eventContext, lifecycleManager) -> {
            // the event can only come from UI

            UserSettings patch = new UserSettings().setResolutionSetting(event.getResolutionSetting());
            var file = getSettingsFile();
            var settings = getCurrentOrDefaultSettings();
            currentSettings = updateSettings(file, settings, patch);


            application.setSettings(getApplicationSettings());
            applyLogicalResolution(application,currentSettings.getResolutionSetting().getWidth(),currentSettings.getResolutionSetting().getHeight());
            application.restart();

            orchestrationBus.publish(new MainThreadRefreshUiEvent(
                    currentSettings.toBuilder().build()
            ), new GameOrchestrationContext(), null);
        };
    }

    @Override
    public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(MainThreadResolutionChangedEvent.class, onResolutionChanged);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(MainThreadResolutionChangedEvent.class, onResolutionChanged);
    }

    private synchronized UserSettings getCurrentOrDefaultSettings() {
        var file = getSettingsFile();

        if (!file.exists()) {
            if (currentSettings == null) {
                saveSettings(file, UserSettings.getDefault());
            } else {
                saveSettings(file, currentSettings);
            }
        }

        try {
            if (currentSettings == null) {
                currentSettings = USER_SETTINGS_FILE_OBJECT_MAPPER.readValue(file, UserSettings.class);
            }

            // Merge missing fields from defaults
            var mergedSettings = USER_SETTINGS_FILE_OBJECT_MAPPER.updateValue(UserSettings.getDefault(), currentSettings);
            if (!mergedSettings.equals(currentSettings)) {
                currentSettings = mergedSettings;
                saveSettings(file, currentSettings);
            }

        } catch (IOException e) {
            throw new UserSettingsException(e.getMessage());
        }

        return currentSettings;
    }

    private synchronized UserSettings updateSettings(File file, UserSettings settings, UserSettings patch) {
        // Merge non-null fields
        try {
            settings = USER_SETTINGS_FILE_OBJECT_MAPPER.updateValue(settings, patch);
        } catch (IOException e) {
            throw new UserSettingsException(e.getMessage());
        }

        saveSettings(file, settings);
        return settings;
    }

    private synchronized void saveSettings(File file, UserSettings settings) {
        try {
            USER_SETTINGS_FILE_OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, settings);
        } catch (IOException e) {
            throw new UserSettingsException(e.getMessage());
        }
    }


    private File getSettingsFile() {
        return new File("settings.json");
    }

    public synchronized AppSettings getApplicationSettings() {
        var userSettings = getCurrentOrDefaultSettings();

        var resolution = userSettings.getResolutionSetting();

        var appSettings = new AppSettings(true);
        appSettings.setWidth(resolution.getWidth());
        appSettings.setHeight(resolution.getHeight());
        appSettings.setFullscreen(userSettings.getFullscreen());
        appSettings.setVSync(userSettings.getVSync());
        appSettings.setFrameRate(userSettings.getMaxFps());
        return appSettings;
    }

    private void applyLogicalResolution(SimpleApplication app, int w, int h) {
        app.getGuiViewPort().getCamera().resize(w, h,true);
        app.getViewPort().getCamera().resize(w, h,true);
        app.getViewPort().setBackgroundColor(app.getViewPort().getBackgroundColor());
    }
}
