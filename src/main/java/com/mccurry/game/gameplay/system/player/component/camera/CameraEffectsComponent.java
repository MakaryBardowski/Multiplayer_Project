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

    public boolean hasSingleInstanceEffect(Class<? extends CameraEffect> effectClass){
        return singleInstanceEffects.containsKey(effectClass);
    }

    public <T extends CameraEffect> @Nullable T removeSingleInstanceEffect(Class<T> singleInstanceEffectClass) {
        return (T) singleInstanceEffects.remove(singleInstanceEffectClass);
    }

    public void addSingleInstanceEffect(CameraEffect singleInstanceEffect){
        singleInstanceEffects.put(singleInstanceEffect.getClass(),singleInstanceEffect);
    }

    public void addMultiInstanceEffect(CameraEffect multiInstanceEffect){
        multiInstanceEffects.add(multiInstanceEffect);
    }

    public void chainMultiInstance(CameraEffect cameraEffect){
        multiInstanceChainedEffectsToAdd.add(cameraEffect);
    }

    public void chainSingleInstance(CameraEffect cameraEffect){
        singleInstanceChainedEffectsToAdd.add(cameraEffect);
    }
}
