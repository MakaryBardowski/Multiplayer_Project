package com.mccurry.game.threading;

import com.jme3.app.SimpleApplication;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GameProcess {
    private final SimpleApplication game;
    private final Thread mainThread;
}
