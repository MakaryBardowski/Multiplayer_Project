package com.mccurry.game.gameplay.sharedcomponents.transformeffect;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.system.player.component.camera.model.TransformEffect;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class TransformEffectsComponent implements Component {
    private final Map<Class<? extends TransformEffect>, TransformEffect> singleInstanceEffects = new HashMap<>();
    private final List<TransformEffect> multiInstanceEffects = new ArrayList<>();

    public <T extends TransformEffect> @Nullable T getSingleInstanceEffect(Class<T> effectClass){
        return (T) singleInstanceEffects.get(effectClass);
    }

    public <T extends TransformEffect> @Nullable T removeSingleInstanceEffect(Class<T> singleInstanceEffectClass) {
        return (T) singleInstanceEffects.remove(singleInstanceEffectClass);
    }

    public void overwriteOrAddSingleInstanceEffect(TransformEffect singleInstanceEffect){
        singleInstanceEffects.put(singleInstanceEffect.getClass(),singleInstanceEffect);
    }

    public void addMultiInstanceEffect(TransformEffect multiInstanceEffect){
        multiInstanceEffects.add(multiInstanceEffect);
    }
}
