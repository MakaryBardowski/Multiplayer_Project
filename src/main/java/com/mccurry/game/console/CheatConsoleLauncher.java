package com.mccurry.game.console;


import com.jme3.app.Application;
import com.jme3.input.controls.ActionListener;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.keyBinding.event.KeyReboundEvent;
import com.mccurry.game.keyBinding.model.PlayerAction;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class CheatConsoleLauncher implements ActionListener, SyncEventHandler<KeyReboundEvent, GameOrchestrationContext>, EventSubscriber<GameOrchestrationContext> {
    private final Application application;

    private final CheatConsoleState cheatConsoleState;
    private String openCheatConsoleMapping;

    public CheatConsoleLauncher(Application application, String openCheatConsoleMapping){
        this.application = application;
        this.openCheatConsoleMapping = openCheatConsoleMapping;
        cheatConsoleState = new CheatConsoleState(application.getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus());
        System.err.println("cheat console listener expects '%s'".formatted(openCheatConsoleMapping));
    }

    public void startListening(){
        application.getInputManager().addListener(this,openCheatConsoleMapping);
    }

    private void stopListening(){
        application.getInputManager().removeListener(this);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals(openCheatConsoleMapping) && !isPressed){
            var stateManager = application.getStateManager();
            if(stateManager.hasState(cheatConsoleState)){
                stateManager.detach(cheatConsoleState);
                return;
            }
            stateManager.attach(cheatConsoleState);
        }
    }

    @Override
    public void handle(@NonNull KeyReboundEvent event, @NonNull GameOrchestrationContext eventContext, @Nullable EventLifecycleManager<KeyReboundEvent> lifecycleManager) {
        if(event.getPlayerAction().equals(PlayerAction.OPEN_CONSOLE)){
            openCheatConsoleMapping = event.getNewMapping();
            stopListening();
            startListening();
        }
    }

    @Override
    public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(KeyReboundEvent.class, this);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(KeyReboundEvent.class, this);
    }
}
