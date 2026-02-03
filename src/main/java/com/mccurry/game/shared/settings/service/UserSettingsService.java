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
import com.mccurry.game.locale.event.MainThreadLocaleChangedEvent;
import com.mccurry.game.shared.settings.event.MainThreadSettingsAppliedEvent;
import com.mccurry.game.shared.settings.exception.UserSettingsException;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.event.MainThreadResizeUiEvent;
import com.mccurry.game.sound.event.MainThreadSoundVolumeChangedEvent;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

public class UserSettingsService implements EventSubscriber<GameOrchestrationContext> {
    private static final ObjectMapper USER_SETTINGS_FILE_OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final EventHandler<MainThreadSettingsAppliedEvent, GameOrchestrationContext> onSettingsApplied;

    private UserSettings currentSettings;

    public UserSettingsService(EventBus<GameOrchestrationContext> orchestrationBus, SimpleApplication application) {
        onSettingsApplied = (SyncEventHandler<MainThreadSettingsAppliedEvent, GameOrchestrationContext>) (event, eventContext, lifecycleManager) -> {
            // the event can only come from UI

            UserSettings patch = event.getNewSettings();
            var file = getSettingsFile();
            var settings = getCurrentOrDefaultSettings();

            var previousResolution = settings.getResolutionSetting();
            var previousLocale = settings.getLocale();

            // SOUND SETTINGS
            var soundSettings = settings.getSoundSettings(); // @JsonMerge updates instance in place, so we cannot compare directly
            var previousMasterVolume = soundSettings.getMasterVolume();
            var previousMusicVolume = soundSettings.getMusicVolume();
            var previousSfxVolume = soundSettings.getSfxVolume();
            var previousGunshotVolume = soundSettings.getGunshotVolume();
            // SOUND SETTINGS

            currentSettings = updateSettings(file, settings, patch);


            application.setSettings(getApplicationSettings());
//            applyLogicalResolution(application,currentSettings.getResolutionSetting().getWidth(),currentSettings.getResolutionSetting().getHeight());

            if(
                    !soundSettings.getMasterVolume().equals(previousMasterVolume) ||
                            !soundSettings.getMusicVolume().equals(previousMusicVolume) ||
                            !soundSettings.getSfxVolume().equals(previousSfxVolume) ||
                            !soundSettings.getGunshotVolume().equals(previousGunshotVolume)
            ){
                var currentSoundSettings = currentSettings.getSoundSettings();
                orchestrationBus.publish(
                        MainThreadSoundVolumeChangedEvent.builder()
                                .masterVolumePercentage(currentSoundSettings.getMasterVolume())
                                .musicVolumePercentage(currentSoundSettings.getMusicVolume())
                                .sfxVolumePercentage(currentSoundSettings.getSfxVolume())
                                .gunshotVolumePercentage(currentSoundSettings.getGunshotVolume())
                                .build(),
                        new GameOrchestrationContext(),
                        null
                );
            }

            if(!currentSettings.getResolutionSetting().equals(previousResolution)) {
                application.restart();
                orchestrationBus.publish(new MainThreadResizeUiEvent(
                        currentSettings.getResolutionSetting().getUiScale()
                ), new GameOrchestrationContext(), null);
            }

            if(!currentSettings.getLocale().equals(previousLocale)){
                orchestrationBus.publish(
                        MainThreadLocaleChangedEvent.builder()
                                .newLocale(currentSettings.getLocale())
                                .build(),
                        new GameOrchestrationContext(),
                        null
                );
            }
        };
    }

    @Override
    public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(MainThreadSettingsAppliedEvent.class, onSettingsApplied);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(MainThreadSettingsAppliedEvent.class, onSettingsApplied);
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
            var defaultSettings = UserSettings.getDefault();

            // Manually merge keybinds (jackson doesnt do that automatically). This happens before "root" options merge because root merge is in place )
            var mergedKeybinds = new LinkedHashMap<>(defaultSettings.getKeyBinds());
            mergedKeybinds.putAll(currentSettings.getKeyBinds());


            var mergedSettings = USER_SETTINGS_FILE_OBJECT_MAPPER.updateValue(defaultSettings, currentSettings);
            mergedSettings.setKeyBinds(mergedKeybinds);


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
            // Manually merge keybinds (jackson doesnt do that automatically). This happens before "root" options merge because root merge is in place )
            var mergedKeybinds = new LinkedHashMap<>(settings.getKeyBinds());
            mergedKeybinds.putAll(patch.getKeyBinds());

            settings = USER_SETTINGS_FILE_OBJECT_MAPPER.updateValue(settings, patch);
            settings.setKeyBinds(mergedKeybinds);
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

    public UserSettings getCurrentSettingsDeepClone(){
        return currentSettings.deepClone();
    }
}
