package com.mccurry.game.gameplayConfig.keyBinding;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameplayConfig.keyBinding.model.KeyBind;
import com.mccurry.game.gameplayConfig.keyBinding.model.PlayerAction;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameplayConfig.keyBinding.event.KeyRebindEvent;
import com.mccurry.game.shared.settings.event.MainThreadSettingsAppliedEvent;
import com.mccurry.game.shared.settings.model.UserSettings;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.mccurry.game.gameplayConfig.keyBinding.KeyBindingRegistry.*;

public class KeyBindingService implements EventSubscriber<GameOrchestrationContext>, SyncEventHandler<MainThreadSettingsAppliedEvent,GameOrchestrationContext> {
    private final EventBus<GameOrchestrationContext> orchestrationBus;
    private final Map<PlayerAction, KeyBind> currentKeyBinds;

    public static KeyBindingService withKeybindsFromSettings(EventBus<GameOrchestrationContext> orchestrationBus, UserSettings userSettings) {
        return new KeyBindingService(orchestrationBus, userSettings);
    }

    private KeyBindingService(EventBus<GameOrchestrationContext> orchestrationBus, UserSettings userSettingsDeepClone) {
        currentKeyBinds = userSettingsDeepClone.getKeyBinds();
        this.orchestrationBus = orchestrationBus;
        subscribe(orchestrationBus);
    }

    public void initializeMappings(InputManager inputManager) {
        inputManager.addMapping(BINDING_MOUSE_LEFT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(BINDING_MOUSE_RIGHT, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping(BINDING_CTRL_LEFT, new KeyTrigger(KeyInput.KEY_LCONTROL));

        inputManager.addMapping("A", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("B", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("C", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("D", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("E", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("F", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("G", new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("H", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("I", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("J", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("K", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("L", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("M", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping("N", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addMapping("O", new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping("P", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Q", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("R", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("S", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("T", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("U", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("V", new KeyTrigger(KeyInput.KEY_V));
        inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("X", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("Y", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("Z", new KeyTrigger(KeyInput.KEY_Z));


        inputManager.addMapping("0", new KeyTrigger(KeyInput.KEY_0), new KeyTrigger(KeyInput.KEY_NUMPAD0));
        inputManager.addMapping("1", new KeyTrigger(KeyInput.KEY_1), new KeyTrigger(KeyInput.KEY_NUMPAD1));
        inputManager.addMapping("2", new KeyTrigger(KeyInput.KEY_2), new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping("3", new KeyTrigger(KeyInput.KEY_3), new KeyTrigger(KeyInput.KEY_NUMPAD3));
        inputManager.addMapping("4", new KeyTrigger(KeyInput.KEY_4), new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping("5", new KeyTrigger(KeyInput.KEY_5), new KeyTrigger(KeyInput.KEY_NUMPAD5));
        inputManager.addMapping("6", new KeyTrigger(KeyInput.KEY_6), new KeyTrigger(KeyInput.KEY_NUMPAD6));
        inputManager.addMapping("7", new KeyTrigger(KeyInput.KEY_7), new KeyTrigger(KeyInput.KEY_NUMPAD7));
        inputManager.addMapping("8", new KeyTrigger(KeyInput.KEY_8), new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addMapping("9", new KeyTrigger(KeyInput.KEY_9), new KeyTrigger(KeyInput.KEY_NUMPAD9));
    }

    public @Nullable KeyBind getKeybindForPlayerAction(PlayerAction playerAction){
        return currentKeyBinds.get(playerAction);
    }

    public void updateBindings(LinkedHashMap<PlayerAction,KeyBind> newBindings) {
        for(var newBindEntry : newBindings.entrySet()){
            var reboundPlayerAction = newBindEntry.getKey();
            var newKeybind = newBindEntry.getValue();

            var previousBind = currentKeyBinds.put(reboundPlayerAction, newKeybind);

            if(!newKeybind.equals(previousBind)){
                orchestrationBus.publish(
                        KeyRebindEvent.builder()
                                .playerAction(reboundPlayerAction)
                                .newMapping(newKeybind.getKeyName())
                                .build(),
                        new GameOrchestrationContext(),
                        null
                );
            }
        }
    }

    @Override
    public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(MainThreadSettingsAppliedEvent.class,this);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(MainThreadSettingsAppliedEvent.class,this);
    }

    @Override
    public void handle(@NonNull MainThreadSettingsAppliedEvent event, @NonNull GameOrchestrationContext eventContext, @Nullable EventLifecycleManager<MainThreadSettingsAppliedEvent> lifecycleManager) {
        updateBindings(event.getNewSettings().getKeyBinds());
    }
}
