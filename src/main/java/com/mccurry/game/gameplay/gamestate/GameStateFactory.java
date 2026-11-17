package com.mccurry.game.gameplay.gamestate;

import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;

public class GameStateFactory {
    public static GameState singleplayer(){
        var entityStore = new EntityStore();
        return new GameState(entityStore);
    }

    public static GameState host(){
        var entityStore = new EntityStore();
        return new GameState(entityStore);
    }
}
