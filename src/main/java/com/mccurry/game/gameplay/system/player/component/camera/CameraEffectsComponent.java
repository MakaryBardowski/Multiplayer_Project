package com.mccurry.game.gameplay.system.player.component.camera;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.system.player.component.camera.model.CameraEffect;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class CameraEffectsComponent implements Component {
    private final Map<Class<? extends CameraEffect>, CameraEffect> singleInstanceEffects = new HashMap<>();
    private final List<CameraEffect> multiInstanceEffects = new ArrayList<>();

    private final List<CameraEffect> multiInstanceChainedEffectsToAdd = new ArrayList<>(16);
    private final List<CameraEffect> singleInstanceChainedEffectsToAdd = new ArrayList<>(8);

    public <T extends CameraEffect> @Nullable T getSingleInstanceEffect(Class<T> effectClass){
        return (T) singleInstanceEffects.get(effectClass);
    }

    public <T extends CameraEffect> @Nullable T removeSingleInstanceEffect(Class<T> singleInstanceEffectClass) {
        return (T) singleInstanceEffects.remove(singleInstanceEffectClass);
    }

    public void overwriteOrAddSingleInstanceEffect(CameraEffect singleInstanceEffect){
        singleInstanceEffects.put(singleInstanceEffect.getClass(),singleInstanceEffect);
    }

    public void addMultiInstanceEffect(CameraEffect multiInstanceEffect){
        multiInstanceEffects.add(multiInstanceEffect);
    }
}
