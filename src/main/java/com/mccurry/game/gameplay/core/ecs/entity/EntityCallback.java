package com.mccurry.game.gameplay.core.ecs.entity;

public interface EntityCallback {
    void execute(Entity entityWithRequiredComponents, float tpf);
}
