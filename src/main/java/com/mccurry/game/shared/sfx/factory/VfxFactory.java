package com.mccurry.game.shared.sfx.factory;

import com.jme3.asset.AssetManager;
import com.mccurry.game.shared.sfx.model.Vfx;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class VfxFactory {
    protected final AssetManager assetManager;
    public VfxFactory(AssetManager assetManager){
        this.assetManager = assetManager;
    }

    public abstract Vfx create();

    public static VfxFactory of(AssetManager assetManager, Consumer<Vfx> onFinishedPlaying, BiFunction<AssetManager, Consumer<Vfx>, Vfx> supplier){
        return new VfxFactory(assetManager) {
            @Override
            public Vfx create() {
                return supplier.apply(this.assetManager,onFinishedPlaying);
            }
        };
    }
}
