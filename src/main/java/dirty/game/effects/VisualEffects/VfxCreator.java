package dirty.game.effects.VisualEffects;

import dirty.client.Main;
import com.epagagames.particles.BillboardMode;
import com.epagagames.particles.Emitter;
import com.epagagames.particles.emittershapes.EmitterCone;
import com.epagagames.particles.influencers.ColorInfluencer;
import com.epagagames.particles.influencers.GravityInfluencer;
import com.epagagames.particles.valuetypes.ColorValueType;
import com.epagagames.particles.valuetypes.Gradient;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.epagagames.particles.valuetypes.ValueType;
import dirty.game.effects.VisualEffects.ParticleInfluencers.CustomSpriteInfluencer;
import dirty.game.effects.VisualEffects.ParticleInfluencers.RandomInitialRotationInfluencer;
import dirty.game.effects.VisualEffects.ParticleInfluencers.VelocityDampingInfluencer;

public class VfxCreator {
    private static final String BULLET_IMPACT_VFX_ROOT_DIR = "Textures/Gameplay/Effects/BulletImpact/";
    private static final String BLOOD_VFX_ROOT_DIR = "Textures/Gameplay/Effects/Blood/";


    public static Emitter getBulletImpactSmokeEmitter() {
        var assetManager = Main.getInstance().getAssetManager();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
        Texture tex = assetManager.loadTexture(BULLET_IMPACT_VFX_ROOT_DIR+"shotDebris.png");
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTexture("Texture", tex);

        var emitter = new Emitter("Smoke", mat, 100,
                new ColorInfluencer(),
                new VelocityDampingInfluencer(0.8f, 0.9f),
                new CustomSpriteInfluencer(),
                new RandomInitialRotationInfluencer()
        );
        emitter.getInfluencer(CustomSpriteInfluencer.class).setSpriteCols(2);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setSpriteRows(2);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setUseRandomImage(true);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setAnimate(false);

        emitter.setBillboardMode(BillboardMode.Camera);

        emitter.getInfluencer(ColorInfluencer.class).setColorOverTime(new ColorValueType(
                new Gradient()
                        .addGradPoint(new ColorRGBA(1, 1, 1, 0.0f), 0.0f)
                        .addGradPoint(new ColorRGBA(1, 1, 1, 1f), 0.1f)
                        .addGradPoint(new ColorRGBA(1, 1, 1, 1f), 0.2f)
                        .addGradPoint(new ColorRGBA(1, 1, 1, 0.0f), 1.0f)
        ));

        emitter.setStartSize(new ValueType(0.3f));
        emitter.setStartSpeed(new ValueType(2.5f));
        emitter.setLifeMinMax(new ValueType(0.4f), new ValueType(0.6f));
        emitter.setLooping(false);
        emitter.setEmissionsPerSecond(0);

        emitter.setParticlesPerEmission(1);
        emitter.setShape(new EmitterCone());
        ((EmitterCone) emitter.getShape()).setRadius(0.1f);
        ((EmitterCone) emitter.getShape()).setAngle(FastMath.PI * 0.05f);
        emitter.setParticlesFollowEmitter(false);
        emitter.reset();
        emitter.setCullHint(Spatial.CullHint.Never);
        return emitter;
    }

    public static Emitter getBulletImpactDebrisEmitter() {
        var assetManager = Main.getInstance().getAssetManager();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
        Texture tex = assetManager.loadTexture(BULLET_IMPACT_VFX_ROOT_DIR+"shotDebrisParticles.png");
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTexture("Texture", tex);

        var emitter = new Emitter("Debris", mat, 100,
                new ColorInfluencer(),
                new VelocityDampingInfluencer(0.8f, 0.98f),
                new CustomSpriteInfluencer(),
                new GravityInfluencer()
                //             new RandomInitialRotationInfluencer()
        );
        emitter.getInfluencer(CustomSpriteInfluencer.class).setSpriteCols(2);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setSpriteRows(2);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setUseRandomImage(true);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setAnimate(false);

        emitter.setBillboardMode(BillboardMode.Camera);

        emitter.getInfluencer(ColorInfluencer.class).setColorOverTime(new ColorValueType(
                new Gradient()
                        .addGradPoint(new ColorRGBA(1, 1, 1, 0.5f), 0.0f)
                        .addGradPoint(new ColorRGBA(1, 1, 1, 0.5f), 0.1f)
                        .addGradPoint(new ColorRGBA(1, 1, 1, 0.0f), 1.0f)
        ));

        emitter.setStartSize(new ValueType(0.03f));
        emitter.setStartSpeed(new ValueType(4.5f));
        emitter.setLifeMinMax(new ValueType(0.5f), new ValueType(0.6f));
        emitter.setLooping(false);
        emitter.setEmissionsPerSecond(0);

        emitter.setParticlesPerEmission(1);
        emitter.setShape(new EmitterCone());
        ((EmitterCone) emitter.getShape()).setRadius(0.03f);
        ((EmitterCone) emitter.getShape()).setAngle(FastMath.PI * 0.2f);
        emitter.setParticlesFollowEmitter(false);
        emitter.reset();
        emitter.setCullHint(Spatial.CullHint.Never);

        return emitter;
    }

    public static Emitter getBulletImpactDustEmitter() {
        var assetManager = Main.getInstance().getAssetManager();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
        Texture tex = assetManager.loadTexture(BULLET_IMPACT_VFX_ROOT_DIR+"shotSmoke.png");
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTexture("Texture", tex);

        var emitter = new Emitter("Dust", mat, 100,
                new ColorInfluencer(),
                new VelocityDampingInfluencer(0.92f, 0.93f),
                new RandomInitialRotationInfluencer()
        );

        emitter.setBillboardMode(BillboardMode.Camera);

        emitter.getInfluencer(ColorInfluencer.class).setColorOverTime(new ColorValueType(
                new Gradient()
                        .addGradPoint(new ColorRGBA(1, 1, 1, 0.3f), 0.0f)
                        .addGradPoint(new ColorRGBA(1, 1, 1, 0.5f), 0.1f)
                        .addGradPoint(new ColorRGBA(1, 1, 1, 0.0f), 1.0f)
        ));

        emitter.setStartSize(new ValueType(0.3f));
        emitter.setStartSpeed(new ValueType(0.05f));
        emitter.setLifeMinMax(new ValueType(1.25f), new ValueType(1.45f));
        emitter.setLooping(false);
        emitter.setEmissionsPerSecond(0);

        emitter.setParticlesPerEmission(1);
        emitter.setShape(new EmitterCone());
        ((EmitterCone) emitter.getShape()).setRadius(0.1f);
        ((EmitterCone) emitter.getShape()).setAngle(FastMath.PI * 0.4f);
        emitter.setParticlesFollowEmitter(false);
        emitter.reset();
        emitter.setCullHint(Spatial.CullHint.Never);

        return emitter;
    }


    public static Emitter getBloodCloudEmitter() {
        var assetManager = Main.getInstance().getAssetManager();
        float colorR = 120/255f;
        float colorG = 6/255f;
        float colorB = 6/255f;
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
        Texture tex = assetManager.loadTexture(BLOOD_VFX_ROOT_DIR+"bloodCloud.png");
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTexture("Texture", tex);

        var emitter = new Emitter("test", mat, 100,
                new ColorInfluencer(),
                new VelocityDampingInfluencer(0.87f, 0.93f),
                new CustomSpriteInfluencer(),
                new RandomInitialRotationInfluencer()
        );
        emitter.getInfluencer(CustomSpriteInfluencer.class).setSpriteCols(2);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setSpriteRows(2);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setUseRandomImage(true);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setAnimate(true);

        emitter.setBillboardMode(BillboardMode.Camera);

        emitter.getInfluencer(ColorInfluencer.class).setColorOverTime(new ColorValueType(
                new Gradient()
                        .addGradPoint(new ColorRGBA(colorR, colorG, colorB, 0.0f), 0.0f)
                        .addGradPoint(new ColorRGBA(colorR, colorG, colorB, 1f), 0.1f)
                        .addGradPoint(new ColorRGBA(colorR, colorG, colorB,  1f), 0.2f)
                        .addGradPoint(new ColorRGBA(colorR, colorG, colorB, 0.0f), 1.0f)
        ));

        emitter.setStartSize(new ValueType(0.3f));
        emitter.setStartSpeed(new ValueType(4f));
        emitter.setLifeMinMax(new ValueType(0.7f), new ValueType(0.7f));
        emitter.setLooping(false);
        emitter.setEmissionsPerSecond(0);

        emitter.setParticlesPerEmission(1);
        emitter.setShape(new EmitterCone());
        ((EmitterCone) emitter.getShape()).setRadius(0.1f);
        ((EmitterCone) emitter.getShape()).setAngle(FastMath.PI * 0.05f);
        emitter.setParticlesFollowEmitter(false);
        emitter.setCullHint(Spatial.CullHint.Never);

        return emitter;
    }

    public static Emitter getBloodParticlesEmitter() {
        var assetManager = Main.getInstance().getAssetManager();
        float colorR = 120/255f;
        float colorG = 6/255f;
        float colorB = 6/255f;
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
        Texture tex = assetManager.loadTexture(BLOOD_VFX_ROOT_DIR+"bloodParticles.png");
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTexture("Texture", tex);

        var emitter = new Emitter("test", mat, 100,
                new ColorInfluencer(),
                new VelocityDampingInfluencer(0.8f, 0.98f),
                new CustomSpriteInfluencer(),
                new GravityInfluencer()
                //             new RandomInitialRotationInfluencer()
        );
        emitter.getInfluencer(CustomSpriteInfluencer.class).setSpriteCols(2);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setSpriteRows(2);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setUseRandomImage(true);
        emitter.getInfluencer(CustomSpriteInfluencer.class).setAnimate(false);

        emitter.setBillboardMode(BillboardMode.Camera);

        emitter.getInfluencer(ColorInfluencer.class).setColorOverTime(new ColorValueType(
                new Gradient()
                        .addGradPoint(new ColorRGBA(colorR, colorG, colorB, 0.5f), 0.0f)
                        .addGradPoint(new ColorRGBA(colorR, colorG, colorB, 0.8f), 0.1f)
                        .addGradPoint(new ColorRGBA(colorR, colorG, colorB, 0.0f), 1.0f)
        ));

        emitter.setStartSize(new ValueType(0.03f));
        emitter.setStartSpeed(new ValueType(4.5f));
        emitter.setLifeMinMax(new ValueType(0.5f), new ValueType(0.6f));
        emitter.setLooping(false);
        emitter.setEmissionsPerSecond(0);

        emitter.setParticlesPerEmission(1);
        emitter.setShape(new EmitterCone());
        ((EmitterCone) emitter.getShape()).setRadius(0.03f);
        ((EmitterCone) emitter.getShape()).setAngle(FastMath.PI * 0.2f);
        emitter.setParticlesFollowEmitter(false);
        emitter.setCullHint(Spatial.CullHint.Never);

        return emitter;
    }
}
