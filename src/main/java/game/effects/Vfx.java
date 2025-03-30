package game.effects;

import com.jme3.texture.Texture;
import client.appStates.ClientGameAppState;
import client.Main;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import game.effects.VisualEffects.PlayableEmitter;
import game.effects.VisualEffects.VfxCreator;
import game.effects.VisualEffects.VisualEffect;

import java.util.LinkedList;
import java.util.Queue;

public class Vfx {
    private static final Queue<VisualEffect> bulletHitImpactVfx = new LinkedList<>();
    private static final Queue<VisualEffect> bulletBloodVfx = new LinkedList<>();

    private static VisualEffect temp1;
    private static final byte BLOOD_POOL_SIZE = 6;
    private static final byte SHOT_SMOKE_POOL_SIZE = 1;

    static {
        for (int i = 0; i < BLOOD_POOL_SIZE; i++) {
            bulletBloodVfx.add(new VisualEffect(
                    new PlayableEmitter( VfxCreator.getBloodCloudEmitter(), smoke -> {
                        for (int j = 0; j < 2; j++) {
                            smoke.emitNextParticle();
                        }
                    }
                    ),
                    new PlayableEmitter( VfxCreator.getBloodParticlesEmitter(), debris -> {
                        for (int j = 0; j < 6; j++) {
                            debris.emitNextParticle();
                        }
                    }
                    )
            ));
        }
        for (int i = 0; i < SHOT_SMOKE_POOL_SIZE; i++) {
            bulletHitImpactVfx.add(new VisualEffect(
                    new PlayableEmitter( VfxCreator.getBulletImpactSmokeEmitter(), smoke -> {
                        for (int j = 0; j < 1; j++) {
                            smoke.emitNextParticle();
                        }
                    }
                    ),
                    new PlayableEmitter( VfxCreator.getBulletImpactDebrisEmitter(), debris -> {
                        for (int j = 0; j < 6; j++) {
                            debris.emitNextParticle();
                        }
                    }
                    ),
                    new PlayableEmitter( VfxCreator.getBulletImpactDustEmitter(), dust -> {
                        for (int j = 0; j < 1; j++) {
                            dust.emitNextParticle();
                        }
                    }
                    )
            ));
        }
    }

    private static VisualEffect getShotBlood() {
        temp1 = bulletBloodVfx.poll();
        bulletBloodVfx.offer(temp1);
        return temp1;
    }

    private static VisualEffect getShotSmoke() {
        temp1 = bulletHitImpactVfx.poll();
        bulletHitImpactVfx.offer(temp1);
        return temp1;
    }

    public static VisualEffect getEffect(VfxType effectType){
        switch (effectType){
            case ON_SHOT_BLOOD:
                return getShotBlood();
            case BULLET_IMPACT:
                return getShotSmoke();
        }
        return null;
    }
}
