package com.mccurry.game.gameplay.system.movement.component;

import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.core.ecs.component.Component;
import lombok.Getter;

public class VelocityComponent implements Component {
    @Getter
    private Vector3f calculatedActualVelocity = new Vector3f();

    @Getter
    private Vector3f desiredVelocity = new Vector3f();
}
