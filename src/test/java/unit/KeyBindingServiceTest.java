package unit;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameplayConfig.keyBinding.KeyBindingService;
import com.mccurry.game.gameplayConfig.keyBinding.model.KeyBind;
import com.mccurry.game.gameplayConfig.keyBinding.model.PlayerAction;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameplayConfig.keyBinding.event.KeyRebindEvent;
import com.mccurry.game.shared.settings.model.UserSettings;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static com.mccurry.game.gameplayConfig.keyBinding.model.PlayerAction.MOVE_FORWARD;
import static com.mccurry.game.gameplayConfig.keyBinding.model.PlayerAction.MOVE_LEFT;
import static org.assertj.core.api.Assertions.assertThat;

public class KeyBindingServiceTest {

    @Test
    public void shouldPublish_KeyRebindEvent_OnlyForChangedBind_OnBindingChange(){
        var orchestrationBus = new EventBus<GameOrchestrationContext>();

        var originalBindings = new LinkedHashMap<PlayerAction,KeyBind>();
        originalBindings.put(MOVE_FORWARD, new KeyBind("W"));
        originalBindings.put(MOVE_LEFT, new KeyBind("A"));


        var keybindService = KeyBindingService.withKeybindsFromSettings(
                orchestrationBus,
                UserSettings.builder()
                        .keyBinds(originalBindings)
                        .build()
        );

        var newBindings = new LinkedHashMap<PlayerAction, KeyBind>();
        var newBinding = new KeyBind("S");
        newBindings.put(MOVE_FORWARD, newBinding);
        newBindings.put(MOVE_LEFT, new KeyBind("A"));

        var keyRebindEventHandler = new SyncEventHandler<KeyRebindEvent, GameOrchestrationContext>(){
            int eventsConsumed;

            @Override
            public void handle(KeyRebindEvent event, GameOrchestrationContext eventContext, @Nullable EventLifecycleManager<KeyRebindEvent> lifecycleManager) {
                eventsConsumed++;
            }
        };
        orchestrationBus.subscribe(KeyRebindEvent.class,keyRebindEventHandler);

        // when
        keybindService.updateBindings(newBindings);

        // then
        var bindAfterUpdate = keybindService.getKeybindForPlayerAction(MOVE_FORWARD);
        assertThat(bindAfterUpdate.getKeyName()).isEqualTo(newBinding.getKeyName());

        assertThat(keyRebindEventHandler.eventsConsumed).isEqualTo(1);
    }
}
