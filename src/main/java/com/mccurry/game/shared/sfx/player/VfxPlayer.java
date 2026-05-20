package com.mccurry.game.shared.sfx.player;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.mccurry.game.shared.sfx.model.VfxType;
import com.mccurry.game.shared.sfx.pool.VfxPool;
import org.jetbrains.annotations.NotNull;

public class VfxPlayer {
    private final VfxPool vfxPool;

    public VfxPlayer(VfxPool vfxPool){
        this.vfxPool = vfxPool;
    }

    public void playEffect(@NotNull VfxType vfxType, Node parentNode, Vector3f localOffset, Quaternion rotation){

        if(vfxType.equals(VfxType.NONE)){
            return;
        }

        var vfx = vfxPool.getCached(vfxType);
        vfx.play(parentNode,localOffset,rotation);
        // we do not reclaim here, but inside vfx.emit()
    }
}
