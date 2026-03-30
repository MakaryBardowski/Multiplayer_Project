package com.mccurry.game.keyBinding;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.keyBinding.model.KeyBind;
import com.mccurry.game.keyBinding.model.PlayerAction;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import com.mccurry.game.shared.settings.event.MainThreadSettingsAppliedEvent;
import com.mccurry.game.shared.settings.model.UserSettings;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.mccurry.game.keyBinding.KeyMappingRegistry.*;

public class KeyMappingService implements EventSubscriber<GameOrchestrationContext>, SyncEventHandler<MainThreadSettingsAppliedEvent, GameOrchestrationContext> {

    private static final Map<PlayerAction, String> MAPPINGS_BY_PLAYER_ACTION = new HashMap<>() {{
        put(PlayerAction.MOVE_FORWARD, MAPPING_KEY_W);
        put(PlayerAction.MOVE_BACKWARD, MAPPING_KEY_S);
        put(PlayerAction.MOVE_LEFT, MAPPING_KEY_A);
        put(PlayerAction.MOVE_RIGHT, MAPPING_KEY_D);
        put(PlayerAction.ATTACK, MAPPING_MOUSE_LEFT);
        put(PlayerAction.INTERACT, MAPPING_KEY_E);
        put(PlayerAction.SKILL_1, MAPPING_KEY_Q);
        put(PlayerAction.SKILL_2, MAPPING_KEY_R);
    }};

    private final Map<PlayerAction, KeyBind> currentKeyBinds;
    private InputManager inputManager;

    public static KeyMappingService withKeybindsFromSettings(EventBus<GameOrchestrationContext> orchestrationBus, UserSettings userSettings) {
        return new KeyMappingService(orchestrationBus, userSettings);
    }

    private KeyMappingService(EventBus<GameOrchestrationContext> orchestrationBus, UserSettings userSettingsDeepClone) {
        currentKeyBinds = userSettingsDeepClone.getKeyBinds();
        subscribe(orchestrationBus);
    }

    public void initializeMappings(InputManager inputManager) {
        this.inputManager = inputManager;

        HashMap<String, Trigger> triggersByMapping = new HashMap<>();

        triggersByMapping.put(MAPPING_MOUSE_MOVE_X_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X,true));
        triggersByMapping.put(MAPPING_MOUSE_MOVE_X_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X,false));

        triggersByMapping.put(MAPPING_MOUSE_MOVE_Y_UP, new MouseAxisTrigger(MouseInput.AXIS_Y,true));
        triggersByMapping.put(MAPPING_MOUSE_MOVE_Y_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y,false));

        triggersByMapping.put(MAPPING_MOUSE_LEFT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        triggersByMapping.put(MAPPING_MOUSE_RIGHT, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        triggersByMapping.put(MAPPING_MOUSE_MIDDLE, new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));

        triggersByMapping.put(MAPPING_KEY_TILDE, new KeyTrigger(KeyInput.KEY_GRAVE));

        triggersByMapping.put(MAPPING_CTRL_LEFT, new KeyTrigger(KeyInput.KEY_LCONTROL));
        triggersByMapping.put(MAPPING_CTRL_RIGHT, new KeyTrigger(KeyInput.KEY_RCONTROL));

        triggersByMapping.put(MAPPING_SHIFT_LEFT, new KeyTrigger(KeyInput.KEY_LSHIFT));
        triggersByMapping.put(MAPPING_SHIFT_RIGHT, new KeyTrigger(KeyInput.KEY_RSHIFT));


        triggersByMapping.put(MAPPING_KEY_A, new KeyTrigger(KeyInput.KEY_A));
        triggersByMapping.put(MAPPING_KEY_B, new KeyTrigger(KeyInput.KEY_B));
        triggersByMapping.put(MAPPING_KEY_C, new KeyTrigger(KeyInput.KEY_C));
        triggersByMapping.put(MAPPING_KEY_D, new KeyTrigger(KeyInput.KEY_D));
        triggersByMapping.put(MAPPING_KEY_E, new KeyTrigger(KeyInput.KEY_E));
        triggersByMapping.put(MAPPING_KEY_F, new KeyTrigger(KeyInput.KEY_F));
        triggersByMapping.put(MAPPING_KEY_G, new KeyTrigger(KeyInput.KEY_G));
        triggersByMapping.put(MAPPING_KEY_H, new KeyTrigger(KeyInput.KEY_H));
        triggersByMapping.put(MAPPING_KEY_I, new KeyTrigger(KeyInput.KEY_I));
        triggersByMapping.put(MAPPING_KEY_J, new KeyTrigger(KeyInput.KEY_J));
        triggersByMapping.put(MAPPING_KEY_K, new KeyTrigger(KeyInput.KEY_K));
        triggersByMapping.put(MAPPING_KEY_L, new KeyTrigger(KeyInput.KEY_L));
        triggersByMapping.put(MAPPING_KEY_M, new KeyTrigger(KeyInput.KEY_M));
        triggersByMapping.put(MAPPING_KEY_N, new KeyTrigger(KeyInput.KEY_N));
        triggersByMapping.put(MAPPING_KEY_O, new KeyTrigger(KeyInput.KEY_O));
        triggersByMapping.put(MAPPING_KEY_P, new KeyTrigger(KeyInput.KEY_P));
        triggersByMapping.put(MAPPING_KEY_Q, new KeyTrigger(KeyInput.KEY_Q));
        triggersByMapping.put(MAPPING_KEY_R, new KeyTrigger(KeyInput.KEY_R));
        triggersByMapping.put(MAPPING_KEY_S, new KeyTrigger(KeyInput.KEY_S));
        triggersByMapping.put(MAPPING_KEY_T, new KeyTrigger(KeyInput.KEY_T));
        triggersByMapping.put(MAPPING_KEY_U, new KeyTrigger(KeyInput.KEY_U));
        triggersByMapping.put(MAPPING_KEY_V, new KeyTrigger(KeyInput.KEY_V));
        triggersByMapping.put(MAPPING_KEY_W, new KeyTrigger(KeyInput.KEY_W));
        triggersByMapping.put(MAPPING_KEY_X, new KeyTrigger(KeyInput.KEY_X));
        triggersByMapping.put(MAPPING_KEY_Y, new KeyTrigger(KeyInput.KEY_Y));
        triggersByMapping.put(MAPPING_KEY_Z, new KeyTrigger(KeyInput.KEY_Z));


        triggersByMapping.put(MAPPING_KEY_0, new KeyTrigger(KeyInput.KEY_0));
        triggersByMapping.put(MAPPING_KEY_1, new KeyTrigger(KeyInput.KEY_1));
        triggersByMapping.put(MAPPING_KEY_2, new KeyTrigger(KeyInput.KEY_2));
        triggersByMapping.put(MAPPING_KEY_3, new KeyTrigger(KeyInput.KEY_3));
        triggersByMapping.put(MAPPING_KEY_4, new KeyTrigger(KeyInput.KEY_4));
        triggersByMapping.put(MAPPING_KEY_5, new KeyTrigger(KeyInput.KEY_5));
        triggersByMapping.put(MAPPING_KEY_6, new KeyTrigger(KeyInput.KEY_6));
        triggersByMapping.put(MAPPING_KEY_7, new KeyTrigger(KeyInput.KEY_7));
        triggersByMapping.put(MAPPING_KEY_8, new KeyTrigger(KeyInput.KEY_8));
        triggersByMapping.put(MAPPING_KEY_9, new KeyTrigger(KeyInput.KEY_9));

        for(var mappingByPlayerAction : MAPPINGS_BY_PLAYER_ACTION.entrySet()){
            var mapping = mappingByPlayerAction.getValue();
            var playerAction = mappingByPlayerAction.getKey();
            var triggerFromSettings = currentKeyBinds.get(playerAction).getTrigger();
            triggersByMapping.put(mapping,triggerFromSettings);
        }

        for(var triggerByMapping: triggersByMapping.entrySet()){
            inputManager.addMapping(triggerByMapping.getKey(), triggerByMapping.getValue());
        }

    }

    public @Nullable KeyBind getKeybindForPlayerAction(PlayerAction playerAction) {
        return currentKeyBinds.get(playerAction);
    }

    public void updateBindings(Map<PlayerAction, KeyBind> newBindings) {
        for (var newBindEntry : newBindings.entrySet()) {
            var reboundPlayerAction = newBindEntry.getKey();
            var newKeybind = newBindEntry.getValue();

            var previousBind = currentKeyBinds.put(reboundPlayerAction, newKeybind);

            if (previousBind.getTrigger().triggerHashCode() == newKeybind.getTrigger().triggerHashCode()) {
                continue;
            }

            try {
                var mapping = MAPPINGS_BY_PLAYER_ACTION.get(reboundPlayerAction);
                inputManager.deleteMapping(mapping);
                System.out.println("deleting trigger " + previousBind.getKeyDisplayName() + " for mapping " + mapping);
                inputManager.addMapping(mapping, newKeybind.getTrigger());
                System.out.println("added trigger " + newKeybind.getKeyDisplayName() + " for mapping " + mapping);
            } catch (Exception ignored) {
                System.err.println("Failed to rebind key... " + ignored.getMessage());
            }

        }
    }

    @Override
    public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(MainThreadSettingsAppliedEvent.class, this);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(MainThreadSettingsAppliedEvent.class, this);
    }

    @Override
    public void handle(@NonNull MainThreadSettingsAppliedEvent event, @NonNull GameOrchestrationContext eventContext, @Nullable EventLifecycleManager<MainThreadSettingsAppliedEvent> lifecycleManager) {
        updateBindings(event.getNewSettings().getKeyBinds());
    }
}
