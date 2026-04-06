package com.mccurry.game.console;


import com.jme3.app.Application;
import com.jme3.input.controls.ActionListener;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import lombok.Getter;

import static com.mccurry.game.keyBinding.KeyMappingRegistry.MAPPING_KEY_TILDE;

public class CheatConsoleLauncher implements ActionListener {
    private final Application application;

    @Getter
    private final CheatConsoleState cheatConsoleState;

    public CheatConsoleLauncher(Application application){
        this.application = application;
        cheatConsoleState = new CheatConsoleState(application.getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus());
    }

    public void startListening(){
        application.getInputManager().addListener(this,MAPPING_KEY_TILDE);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals(MAPPING_KEY_TILDE) && !isPressed){
            var stateManager = application.getStateManager();
            if(stateManager.hasState(cheatConsoleState)){
                stateManager.detach(cheatConsoleState);
                return;
            }
            stateManager.attach(cheatConsoleState);
        }
    }

}
