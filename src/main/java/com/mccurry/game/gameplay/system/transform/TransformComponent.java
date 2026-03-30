package com.mccurry.game.gameplay.system.transform;

import com.jme3.scene.Node;
import com.mccurry.game.gameplay.core.ecs.component.Component;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class TransformComponent implements Component {
    private final @NotNull Node originNode = new Node();
    private final @NotNull Node visualNode = new Node();

    public TransformComponent(){
        originNode.attachChild(visualNode);
    }
}
