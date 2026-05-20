package com.mccurry.game.shared.sfx.factory;

import com.jme3.asset.AssetManager;
import com.mccurry.game.shared.sfx.model.Vfx;
import com.mccurry.game.shared.sfx.model.VfxType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class VfxFactories {
    public static Map<VfxType, VfxFactory> getFactories(AssetManager assetManager, Consumer<Vfx> onFinishedPlaying){
        var factories = new HashMap<VfxType, VfxFactory>();
        for(var vfxType : VfxType.values()){
            if(vfxType.equals(VfxType.NONE)){
                continue;
            }
            factories.put(vfxType, VfxFactory.of(assetManager,onFinishedPlaying,vfxType.getSupplier()));
        }
        return factories;
    }
}
