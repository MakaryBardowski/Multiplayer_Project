package com.mccurry.game.shared.sfx.pool;

import com.mccurry.game.shared.sfx.factory.VfxFactory;
import com.mccurry.game.shared.sfx.model.Vfx;
import com.mccurry.game.shared.sfx.model.VfxType;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@NoArgsConstructor
public class VfxPool {
    private final Map<VfxType, Queue<Vfx>> vfxPool = new HashMap<>();
    private Map<VfxType, VfxFactory> vfxFactories;

    public void initialize(Map<VfxType, VfxFactory> vfxFactories){
        this.vfxFactories = new HashMap<>(vfxFactories);
    }

    public @NotNull Vfx getCached(VfxType vfxType){
        var queue = vfxPool.get(vfxType);

        if(queue == null){
            vfxPool.put(vfxType, new ArrayDeque<>());
            return vfxFactories.get(vfxType).create();
        }

        var vfx = queue.poll();
        if(vfx == null){
            return vfxFactories.get(vfxType).create();
        }

        return vfx;
    }

    public void reclaim(@NotNull Vfx vfx){
        vfxPool.computeIfAbsent(
                vfx.getType(),
                k -> new ArrayDeque<>()
        ).offer(vfx);
    }
}
