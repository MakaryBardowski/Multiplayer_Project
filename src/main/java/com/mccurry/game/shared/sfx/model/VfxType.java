package com.mccurry.game.shared.sfx.model;

import com.epagagames.particles.BillboardMode;
import com.epagagames.particles.Emitter;
import com.epagagames.particles.emittershapes.EmitterCone;
import com.epagagames.particles.influencers.ColorInfluencer;
import com.epagagames.particles.influencers.GravityInfluencer;
import com.epagagames.particles.valuetypes.ColorValueType;
import com.epagagames.particles.valuetypes.Gradient;
import com.epagagames.particles.valuetypes.ValueType;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.texture.Texture;
import com.mccurry.game.util.debug.GeometryUtil;
import dirty.game.effects.VisualEffects.ParticleInfluencers.CustomSpriteInfluencer;
import dirty.game.effects.VisualEffects.ParticleInfluencers.RandomInitialRotationInfluencer;
import dirty.game.effects.VisualEffects.ParticleInfluencers.VelocityDampingInfluencer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public enum VfxType {
    NONE(null),
    SMG_BULLET_HIT(new BiFunction<>() {
        @Override
        public Vfx apply(AssetManager assetManager, Consumer<Vfx> onFinishedPlaying) {
            var smoke = getBulletImpactSmokeEmitter(assetManager);
            var debris = getBulletImpactDebrisEmitter(assetManager);


            return new Vfx(onFinishedPlaying) {
                private static final Random RANDOM = new Random();
                @Override
                public VfxType getType() {
                    return SMG_BULLET_HIT;
                }

                @Override
                public void play(Node parentNode, Vector3f localOffset, Quaternion localRotation) {
//                    var arrow = GeometryUtil.createArrow(1,Vector3f.UNIT_Z.clone(), ColorRGBA.Blue, assetManager);
//                    parentNode.attachChild(arrow);
//                    arrow.setLocalRotation(localRotation);
//                    arrow.setLocalTranslation(localOffset);


                    // nie robic ich pod jednym parent nodem - wtedy czasem nie dziala...
                    debris.setLocalRotation(localRotation);
                    debris.setLocalTranslation(localOffset);
                    smoke.setLocalRotation(localRotation);
                    smoke.setLocalTranslation(localOffset);

                    parentNode.attachChild(debris);
                    parentNode.attachChild(smoke);


                    debris.rotate(FastMath.DEG_TO_RAD*90,0,0);
                    smoke.rotate(FastMath.DEG_TO_RAD*90,0,0);

                    smoke.emitNextParticle();
                    for(int i = 0; i < RANDOM.nextInt(5,8); i++) {
                        debris.emitNextParticle();
                    }

                    onFinishedPlaying.accept(this);
                }
            };
        }
    }),
    FP__BULLET_CASE__SMG_1(new BiFunction<>() {
        @Override
        public Vfx apply(AssetManager assetManager, Consumer<Vfx> onFinishedPlaying) {
            return new Vfx(onFinishedPlaying) {
                private static final Random RANDOM = new Random();
                @Override
                public VfxType getType() {
                    return FP__BULLET_CASE__SMG_1;
                }

                @Override
                public void play(Node parentNode, Vector3f localOffset, Quaternion localRotation) {
                    var bullet = (Node)assetManager.loadModel("Models/mp18_fp_bullet/mp18_fp_bullet.j3o");
                    bullet = setupModelLight(bullet,assetManager);
                    var horizontalSpeed = RANDOM.nextFloat(3f,4f)*3f;
                    var verticalSpeed = RANDOM.nextFloat(3.5f,4.25f);
                    var depthSpeed = RANDOM.nextFloat(0.8f,1f);
                    bullet.rotate(-FastMath.DEG_TO_RAD*90,0,0);
                    bullet.addControl(new AbstractControl() {

                        private float time = 0f;

                        // random rotation speeds
                        private final float rotX = 4+(FastMath.nextRandomFloat() * 6f);
                        private final float rotY = 4+(FastMath.nextRandomFloat() * 6f);
                        private final float rotZ = 4+(FastMath.nextRandomFloat() * 6f);


                        // movement settings
                        private final float riseSpeed = verticalSpeed;
                        private final float gravity = 14.81f;
                        private float velocityY = riseSpeed;

                        @Override
                        protected void controlUpdate(float tpf) {

                            time += tpf;

                            // simulate shell ejection arc
                            velocityY -= gravity * tpf;
                            spatial.move(
                                    tpf*horizontalSpeed,
                                    tpf*depthSpeed,
                                    velocityY * tpf
                            );

                            // random spinning
                            spatial.rotate(
                                    rotX * tpf,
                                    rotY * tpf,
                                    rotZ * tpf
                            );

                            // optional: remove after some time
                            if (time > 3f) {
                                spatial.removeFromParent();
                            }
                        }

                        @Override
                        protected void controlRender(RenderManager rm, ViewPort vp) {
                        }
                    });

                    parentNode.attachChild(bullet);
                    bullet.setLocalTranslation(localOffset);
                    onFinishedPlaying.accept(this);
                }
            };
        }
    }),
    FP__MUZZLE_FLASH__SMG_1(new BiFunction<>() {
        @Override
        public Vfx apply(AssetManager assetManager, @NotNull Consumer<Vfx> onFinishedPlaying) {
            // Create emitter
            ParticleEmitter emitter = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 50);
            emitter.setParticlesPerSec(0f);

            // Material with your spritesheet
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            System.out.println(mat.getAdditionalRenderState().getBlendMode() + " <------- blend");
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
            var tex = assetManager.loadTexture("Textures/Gameplay/SFX/Muzzle_Flash_1.png");

            tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
            tex.setMagFilter(Texture.MagFilter.Nearest);
            mat.setTexture("Texture", tex);
            mat.setTexture("GlowMap", tex);

            emitter.setMaterial(mat);

            // IMPORTANT: 32x32 spritesheet
            emitter.setImagesX(2);
            emitter.setImagesY(2);

            // Particle behavior
            emitter.setStartColor(ColorRGBA.White);
            emitter.setEndColor(ColorRGBA.White);
            emitter.setStartSize(0.5f);
            emitter.setEndSize(1.25f);

            emitter.setGravity(0, 0, 0);
            emitter.setLowLife(3f / 60f);
            emitter.setHighLife(3f / 60);

            emitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0));
            emitter.getParticleInfluencer().setVelocityVariation(0f);


            // Optional: random frame selection
            emitter.setSelectRandomImage(false);
            emitter.setInWorldSpace(false);

            emitter.setQueueBucket(RenderQueue.Bucket.Transparent);
            emitter.setShadowMode(RenderQueue.ShadowMode.Off);
            return new Vfx(onFinishedPlaying) {
                @Override
                public VfxType getType() {
                    return FP__MUZZLE_FLASH__SMG_1;
                }

                @Override
                public void play(Node parentNode, Vector3f localOffset, Quaternion localRotation) {
                    parentNode.attachChild(emitter);
                    emitter.setLocalTranslation(localOffset);
                    emitter.emitParticles(1);
                    onFinishedPlaying.accept(this);
                }
            };
        }
    });
    private final BiFunction<AssetManager, Consumer<Vfx>, Vfx> supplier;
    public static Node setupModelLight(Node node, AssetManager assetManager) {
        for (Spatial c : node.getChildren()) {

            if (c != null) {
                if (c instanceof Geometry g) {
                    Material originalMaterial = g.getMaterial();

                    if (originalMaterial.getTextureParam("BaseColorMap") != null) {
                        Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
                        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
                        g.setMaterial(newMaterial);
                    }
                } else if (c instanceof Node n) {
                    setupModelLight(n,assetManager);
                }
            }
        }
        return node;
    }


    public static Emitter getBulletImpactSmokeEmitter(AssetManager assetManager) {

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        Texture tex = assetManager.loadTexture("Textures/Gameplay/Effects/BulletImpact/shotDebris.png");
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
        var oneVoxelParticleSize = 1.5f/32f;
        emitter.setStartSize(new ValueType(oneVoxelParticleSize*8));
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
        emitter.setQueueBucket(RenderQueue.Bucket.Opaque); // workaround, inaczej renderuje sie nad wszystkim :)
        return emitter;
    }

    public static Emitter getBulletImpactDebrisEmitter(AssetManager assetManager) {

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        Texture tex = assetManager.loadTexture("Textures/Gameplay/Effects/BulletImpact/"+"shotDebrisParticles.png");
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

        var oneVoxelParticleSize = 1.5f/32f;
        emitter.setStartSize(new ValueType(oneVoxelParticleSize));
        emitter.setStartSpeed(new ValueType(6f));
        emitter.setLifeMinMax(new ValueType(0.5f), new ValueType(0.6f));
        emitter.setLooping(false);
        emitter.setEmissionsPerSecond(0);

        emitter.setParticlesPerEmission(1);
        emitter.setShape(new EmitterCone());
        ((EmitterCone) emitter.getShape()).setRadius(0.05f);
        ((EmitterCone) emitter.getShape()).setAngle(FastMath.PI * 0.4f);
        emitter.setParticlesFollowEmitter(false);
        emitter.reset();
        emitter.setCullHint(Spatial.CullHint.Never);
        emitter.setQueueBucket(RenderQueue.Bucket.Opaque); // workaround, inaczej renderuje sie nad wszystkim :)
        return emitter;
    }
}