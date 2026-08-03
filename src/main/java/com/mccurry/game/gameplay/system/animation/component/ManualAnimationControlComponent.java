package com.mccurry.game.gameplay.system.animation.component;

import com.jme3.anim.Joint;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.system.animation.control.ManualAnimationControl;
import com.mccurry.game.gameplay.system.raycast.entityraycast.model.EntityRaycastHitbox;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ManualAnimationControlComponent implements Component {
    @NotNull private final ManualAnimationControl manualAnimationControl;

    public void rotateBy(Joint joint, Quaternion rotation) {
        manualAnimationControl.rotateBy(joint,rotation);
    }

    public void translateBy(Joint joint, Vector3f translation) {
        manualAnimationControl.translateBy(joint,translation);
    }

    public void rotateBy(EntityRaycastHitbox hitbox, Quaternion rotation) {
        manualAnimationControl.rotateBy(hitbox,rotation);

    }

    public void translateBy(EntityRaycastHitbox hitbox, Vector3f translation) {
        manualAnimationControl.translateBy(hitbox,translation);

    }
}
