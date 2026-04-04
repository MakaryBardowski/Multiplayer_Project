package com.mccurry.game.gameplay.core.ecs.system;

public interface QueueSystem {
    void processQueued(float tpf, double gameTimeSeconds);
}
