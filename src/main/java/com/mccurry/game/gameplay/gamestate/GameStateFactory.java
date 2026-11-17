package com.mccurry.game.gameplay.gamestate;

public class GameStateFactory {
    public static GameState singleplayer(){
        return new GameState();
    }

    public static GameState host(){
        return new GameState();
    }
}
