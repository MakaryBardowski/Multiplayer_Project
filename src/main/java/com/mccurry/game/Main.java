package com.mccurry.game;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;

public class Main extends SimpleApplication {
    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.White);
        System.out.println("\n\n\n\n[Main] Starting");
        stateManager.attach(new GameStateOrchestrator());
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
}
