package com.mccurry.game.gameplay.system.attack.core;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.core.component.WeaponIdentityComponent;
import com.mccurry.game.gameplay.system.attack.core.component.viewmodel.WeaponViewModelComponent;
import com.mccurry.game.gameplay.system.attack.core.model.AttackAction;
import com.mccurry.game.gameplay.system.crosshair.component.CrosshairSourceComponent;
import com.mccurry.game.gameplay.system.crosshair.event.CrosshairSpreadRequestEvent;
import com.mccurry.game.gameplay.system.damage.event.DamageRequestEvent;
import com.mccurry.game.gameplay.system.damage.model.DamageInstance;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.FirstPersonViewTransformEffectsComponent;
import com.mccurry.game.gameplay.system.hitmarker.event.HitmarkerTriggerEvent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraTransformEffectsComponent;
import com.mccurry.game.gameplay.system.raycast.entityraycast.EntityRaycastSystem;
import com.mccurry.game.gameplay.system.raycast.mapraycast.MapRaycastSystem;
import com.mccurry.game.gameplay.system.raycast.model.RaycastResultSelector;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import com.mccurry.game.shared.sfx.model.VfxType;
import com.mccurry.game.shared.sfx.player.VfxPlayer;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pocs.SmgCameraRecoilTransformEffect;
import pocs.SmgViewmodelRecoilTransformEffect;

import java.util.Random;

import static com.mccurry.game.gameplay.system.attack.core.component.WeaponIdentityComponent.WeaponActionIdentity.WeaponProjectileSpawnConfig.ProjectileRelativeSpreadStrategy.FULL_RANDOM;

@RequiredArgsConstructor
public class AttackSystem {
    private static final Random RANDOM = new Random();

    private final MapRaycastSystem mapRaycastSystem;
    private final EntityRaycastSystem entityRaycastSystem;
    private final EventBus eventBus;
    private final EntityStore entityStore;
    private final VfxPlayer vfxPlayer;

    public void processAttack(
            Entity attacker,
            Entity weaponEntity,
            @NotNull WeaponIdentityComponent weaponIdentityComponent,
            AttackAction action,
            float tpf,
            double gameTimeSeconds
    ) {
        var weaponActionIdentity = switch (action) {
            case PRIMARY -> weaponIdentityComponent.getPrimaryActionIdentity();
            case SECONDARY -> weaponIdentityComponent.getSecondaryActionIdentity();
        };

        if (weaponActionIdentity == null || weaponActionIdentity.getLastUsedGameTimeSeconds() + weaponActionIdentity.getCooldownSeconds() > gameTimeSeconds) {
            return;
        }


        var actualMinSpreadAngle = 0f;
        var actualMaxSpreadAngle = 0f;
        var crosshairSourceComponent = entityStore.getComponent(weaponEntity, CrosshairSourceComponent.class);
        if (crosshairSourceComponent != null) {

            var crosshairSpreadEvent = new CrosshairSpreadRequestEvent();

            var weaponSpread = weaponIdentityComponent.getSpreadData();
            if (weaponSpread != null) {
                var weaponHeat = weaponSpread.getHeat();
                var currentHeatLevel = weaponHeat.getLevel();

                var secondsElapsedSinceLastActivation = gameTimeSeconds - weaponActionIdentity.getLastUsedGameTimeSeconds();
                var heatChangeSinceLastActivation = weaponHeat.getLevelChangeRatePerSecond() * secondsElapsedSinceLastActivation;

                currentHeatLevel = (float) Math.clamp(currentHeatLevel + heatChangeSinceLastActivation, 0, weaponHeat.getMaxLevel());

                var minSpreadAngle = weaponSpread.getMinimumSpread().getAngle();
                var maxSpreadAngle = weaponSpread.getMaximumSpread().getAngle();

                var maxHeatLevel = weaponHeat.getMaxLevel();

                var thisShotMaxAngle = calculateSpreadMaxAngle(minSpreadAngle, maxSpreadAngle, currentHeatLevel, maxHeatLevel);
                System.err.println("new max spread %s".formatted(thisShotMaxAngle));
                actualMaxSpreadAngle = thisShotMaxAngle;

                currentHeatLevel = Math.clamp(currentHeatLevel + weaponHeat.getHeatChangePerActivation(), 0, maxHeatLevel);
                weaponHeat.setLevel(currentHeatLevel);


                var nextShotMaxAngle = calculateSpreadMaxAngle(minSpreadAngle, maxSpreadAngle, currentHeatLevel, maxHeatLevel);
                crosshairSpreadEvent.setNewAngle(nextShotMaxAngle);
            }

            eventBus.publish(crosshairSpreadEvent, null);
        }


        // assume all weaps are raycast for now

        var attackerTransform = entityStore.getComponent(attacker, TransformComponent.class);
        var attackerCameraComponent = entityStore.getComponent(attacker, CameraHolderComponent.class);

        if (attackerCameraComponent != null && attackerTransform != null) {

            var projectileConfig = weaponActionIdentity.getProjectileConfig();

            var baseDamage = projectileConfig.getProjectileIdentity().getDamage();


            switch (projectileConfig.getProjectileType()) {
                case HITSCAN -> {
                    var bulletCount = projectileConfig.getProjectileCount();

                    for (int bulletIndex = 0; bulletIndex < bulletCount; bulletIndex++) {

                        var camOffset = attackerCameraComponent.getCurrentActualCameraOffset();
                        var dir = attackerCameraComponent.getCurrentActualCameraRotation().mult(Vector3f.UNIT_Z);

                        if (actualMinSpreadAngle < actualMaxSpreadAngle) {
                            dir = getRandomDirectionInCone(
                                    attackerCameraComponent.getCurrentActualCameraRotation(),
                                    RANDOM.nextFloat(actualMinSpreadAngle, actualMaxSpreadAngle) * FastMath.DEG_TO_RAD,
                                    projectileConfig.getRelativeSpreadStrategy(),
                                    bulletIndex,
                                    bulletCount
                            );
                        }

                        var camPos = attackerTransform.getOriginNode().getLocalTranslation().clone().add(camOffset);

                        var mapRaycastResult = mapRaycastSystem.getOneRaycastResult(camPos, dir, RaycastResultSelector.CLOSEST);
                        var entityRaycastResult = entityRaycastSystem.getOneRaycastResult(attacker.getId(), camPos, dir, RaycastResultSelector.CLOSEST, tpf, gameTimeSeconds);

                        boolean isMapCollisionValid = false;
                        boolean isEntityCollisionValid = false;

                        if (mapRaycastResult != null) {
                            isMapCollisionValid = true;
                        }

                        if (entityRaycastResult != null) {
                            isEntityCollisionValid = true;
                        }

                        if (isEntityCollisionValid && isMapCollisionValid) {
                            var mapCollisionDistance = camPos.distance(mapRaycastResult.getContactPoint());
                            var entityCollisionDistance = camPos.distance(entityRaycastResult.getContactPoint());
                            isMapCollisionValid = mapCollisionDistance < entityCollisionDistance;
                            isEntityCollisionValid = !isMapCollisionValid;
                        }

                        if (isMapCollisionValid) {
                            Vector3f normal = mapRaycastResult.getContactNormal().normalize();

                            Quaternion rot = new Quaternion();
                            rot.lookAt(normal, Vector3f.UNIT_Y);

                            vfxPlayer.playEffect(
                                    VfxType.SMG_BULLET_HIT,
                                    attackerTransform.getOriginNode().getParent(),
                                    mapRaycastResult.getContactPoint().add(mapRaycastResult.getContactNormal().normalize().multLocal(0.05f)),
                                    rot);
                        }

                        if (isEntityCollisionValid) {

                            Vector3f normal = entityRaycastResult.getContactNormal().normalize();

                            Quaternion rot = new Quaternion();
                            rot.lookAt(normal, Vector3f.UNIT_Y);

                            vfxPlayer.playEffect(
                                    VfxType.BLOOD_HEADSHOT,
                                    attackerTransform.getOriginNode().getParent(),
                                    entityRaycastResult.getContactPoint().add(entityRaycastResult.getContactNormal().normalize().multLocal(0.05f)),
                                    rot);

                            eventBus.publish(new DamageRequestEvent(
                                            entityRaycastResult.getHitEntityId(),
                                            new DamageInstance(new DamageInstance.DamageTypeInstance(baseDamage)),
                                            (damageResult -> {
                                                eventBus.publish(new HitmarkerTriggerEvent(damageResult.isTargetDied()), null);

                                            })
                                    ),
                                    null
                            );
                        }

                    }

                    break;
                }


            }

        }

        var gunViewModelComponent = entityStore.getComponent(weaponEntity, WeaponViewModelComponent.class);

        if (gunViewModelComponent != null) {
            var gunVfx = gunViewModelComponent.getPrimaryActionVfxData();

            if (action.equals(AttackAction.SECONDARY)) {
                gunVfx = gunViewModelComponent.getSecondaryVfxData();
            }


            if (gunVfx != null && gunVfx.getMuzzleNode() != null && gunVfx.getMuzzleFlash() != null) {
                vfxPlayer.playEffect(gunVfx.getMuzzleFlash(), gunVfx.getMuzzleNode(), gunVfx.getMuzzleFlashOffset(), Quaternion.IDENTITY.clone());
            }

            if (gunVfx != null && gunVfx.getBulletCaseEjectionNode() != null && gunVfx.getCaseEjection() != null) {
                vfxPlayer.playEffect(gunVfx.getCaseEjection(), gunVfx.getBulletCaseEjectionNode(), new Vector3f(0, 0, 0), Quaternion.IDENTITY.clone());
            }

            var animData = gunViewModelComponent.getGunAnimationData();
            var animComposer = gunViewModelComponent.getAnimComposer();
            if (animData != null && animComposer != null) {
                var recoilAnim = animData.getLockRecoilAnimName();
                if (recoilAnim != null && animComposer.getAnimClipsNames().contains(recoilAnim)) {
                    animComposer.setCurrentAction(recoilAnim);
                }
            }
        }

        var viewmodelEffectsComponent = entityStore.getComponent(attacker, FirstPersonViewTransformEffectsComponent.class);
        if (viewmodelEffectsComponent != null) {
            var viewmodelRecoil = new SmgViewmodelRecoilTransformEffect();
            viewmodelRecoil.setEnabled(true);
            viewmodelRecoil.onShot();
            viewmodelEffectsComponent.addMultiInstanceEffect(
                    viewmodelRecoil
            );
        }

        var cameraEffectsComponent = entityStore.getComponent(attacker, CameraTransformEffectsComponent.class);
        if (cameraEffectsComponent != null) {
            var cameraRecoil = new SmgCameraRecoilTransformEffect();
            cameraRecoil.setEnabled(true);
            cameraRecoil.onShot();
            cameraEffectsComponent.addMultiInstanceEffect(
                    cameraRecoil
            );
        }


        // must be last
        weaponActionIdentity.setLastUsedGameTimeSeconds(gameTimeSeconds);
    }


//    private static Vector3f getRandomDirectionInCone(
//            Quaternion cameraRotation,
//            float coneAngle
//    ) {
//        // Random azimuth around the cone
//        float theta = FastMath.TWO_PI * FastMath.nextRandomFloat();
//
//        // Random point inside the cone.
//        // This gives a uniform distribution over solid angle.
//        float cosTheta = FastMath.cos(coneAngle);
//        float cosPhi = 1.0f - FastMath.nextRandomFloat() * (1.0f - cosTheta);
//        float sinPhi = FastMath.sqrt(1.0f - cosPhi * cosPhi);
//
//        // Direction in local space.
//        // JME's UNIT_Z is the forward direction used here.
//        Vector3f localDirection = new Vector3f(
//                sinPhi * FastMath.cos(theta),
//                sinPhi * FastMath.sin(theta),
//                cosPhi
//        );
//
//        // Transform local direction into camera/world space.
//        return cameraRotation.mult(localDirection);
//    }


    private static Vector3f getRandomDirectionInCone(
            Quaternion cameraRotation,
            float coneAngle,
            WeaponIdentityComponent.WeaponActionIdentity.WeaponProjectileSpawnConfig.ProjectileRelativeSpreadStrategy spreadStrategy,
            int pelletIndex,
            int pelletCount
    ) {
        final float cosConeAngle = FastMath.cos(coneAngle);

        float azimuth;
        float cosPhi;

        switch (spreadStrategy) {
            case FULL_RANDOM -> {
                // Uniform random distribution over solid angle.
                azimuth = FastMath.TWO_PI * FastMath.nextRandomFloat();
                cosPhi = 1.0f
                        - FastMath.nextRandomFloat()
                        * (1.0f - cosConeAngle);
            }

            case SUNFLOWER -> {
                // Golden angle in radians.
                final float goldenAngle = 2.39996323f;

                /*
                 * Distribute points uniformly by area over the cone.
                 *
                 * +0.5 prevents a pellet from landing exactly at the
                 * center or exactly on the outer boundary.
                 */
                float t = (pelletIndex + 0.5f) / pelletCount;

                azimuth = pelletIndex * goldenAngle;

                // Uniform distribution over solid angle.
                cosPhi = 1.0f - t * (1.0f - cosConeAngle);
            }

            default -> throw new IllegalArgumentException(
                    "Unknown spread strategy: " + spreadStrategy
            );
        }

        float sinPhi = FastMath.sqrt(1.0f - cosPhi * cosPhi);

        Vector3f localDirection = new Vector3f(
                sinPhi * FastMath.cos(azimuth),
                sinPhi * FastMath.sin(azimuth),
                cosPhi
        );

        return cameraRotation.mult(localDirection);
    }

    private static float calculateSpreadMaxAngle(float minSpreadAngle, float maxSpreadAngle, float currentHeatLevel, float maxHeatLevel) {
        return minSpreadAngle + ((float) currentHeatLevel / maxHeatLevel) * (maxSpreadAngle - minSpreadAngle);
    }
}
