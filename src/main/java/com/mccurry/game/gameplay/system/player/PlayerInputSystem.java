package com.mccurry.game.gameplay.system.player;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Vector3f;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.intent.event.IntentEvent;
import com.mccurry.game.gameplay.system.intent.model.Intent;
import com.mccurry.game.gameplay.system.movement.component.VelocityComponent;
import com.mccurry.game.gameplay.system.player.component.control.PlayerControlledComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import com.mccurry.game.keyBinding.KeyMappingRegistry;

import java.util.HashSet;
import java.util.Set;

public class PlayerInputSystem implements ActionListener, AnalogListener {
    private final EntityStore entityStore;
    private final EventBus orchestrationBus;

    private final Set<String> activeInputs = new HashSet<>();

    private final Vector3f temp = new Vector3f();
    private final Vector3f temp2 = new Vector3f();
    private final Vector3f temp3 = new Vector3f();

    public PlayerInputSystem(EntityStore entityStore, EventBus orchestrationBus) {
        this.entityStore = entityStore;
        this.orchestrationBus = orchestrationBus;
    }


    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            activeInputs.add(name);
        } else {
            activeInputs.remove(name);
        }
    }


    private void updateInput() {
        var playerControlledEntity = entityStore.getSingletonComponentHolder(PlayerControlledComponent.class);
        if (playerControlledEntity == null) {
            return;
        }

        var velocity = entityStore.getComponent(playerControlledEntity, VelocityComponent.class);
        if (velocity == null) {
            return;
        }

        var transform = entityStore.getComponent(playerControlledEntity, TransformComponent.class);
        if (transform == null) {
            return;
        }


        temp.set(0, 0, 0);

        var rotation = transform
                .getVisualNode()
                .getLocalRotation();


        rotation.getRotationColumn(2, temp2).normalizeLocal();
        rotation.getRotationColumn(0, temp3).normalizeLocal();


        if (activeInputs.contains(KeyMappingRegistry.MAPPING_KEY_W)) {
            temp.addLocal(temp2.mult(20));
        }

        if (activeInputs.contains(KeyMappingRegistry.MAPPING_KEY_S)) {
            temp.addLocal(temp2.negate().multLocal(10));
        }

        if (activeInputs.contains(KeyMappingRegistry.MAPPING_KEY_A)) {
            temp.addLocal(temp3.mult(20));
        }

        if (activeInputs.contains(KeyMappingRegistry.MAPPING_KEY_D)) {
            temp.addLocal(temp3.negate().multLocal(20));
        }

        velocity.getDesiredVelocity().set(temp);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        updateInput();


        if (name.equals(KeyMappingRegistry.MAPPING_MOUSE_LEFT)) {
            var playerControlledEntity = entityStore.getSingletonComponentHolder(PlayerControlledComponent.class);
            if (playerControlledEntity == null) {
                return;
            }
            orchestrationBus.publish(
                    new IntentEvent(Intent.PRIMARY_ACTION, playerControlledEntity),
                    null
            );
        }
    }
}
