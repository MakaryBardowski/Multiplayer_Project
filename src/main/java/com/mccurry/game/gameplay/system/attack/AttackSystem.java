package com.mccurry.game.gameplay.system.attack;

import com.jme3.anim.AnimComposer;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.component.WeaponIdentityComponent;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.GunAnimationData;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.GunVfxData;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.WeaponViewModelComponent;
import com.mccurry.game.gameplay.system.attack.model.AttackAction;
import com.mccurry.game.gameplay.system.crosshair.component.CrosshairSourceComponent;
import com.mccurry.game.gameplay.system.crosshair.event.CrosshairSpreadRequestEvent;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.FirstPersonViewTransformEffectsComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraTransformEffectsComponent;
import com.mccurry.game.gameplay.system.raycast.entityraycast.EntityRaycastSystem;
import com.mccurry.game.gameplay.system.raycast.mapraycast.MapRaycastSystem;
import com.mccurry.game.gameplay.system.raycast.model.RaycastResultSelector;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import com.mccurry.game.shared.sfx.model.VfxType;
import com.mccurry.game.shared.sfx.player.VfxPlayer;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import pocs.SmgCameraRecoilTransformEffect;
import pocs.SmgViewmodelRecoilTransformEffect;

@RequiredArgsConstructor
public class AttackSystem {
    private final MapRaycastSystem mapRaycastSystem;
    private final EntityRaycastSystem entityRaycastSystem;
    private final EventBus eventBus;
    private final EntityStore entityStore;
    private final VfxPlayer vfxPlayer;

    public void processAttack(
            Entity attacker,
            Entity weaponEntity,
            @Nullable WeaponIdentityComponent.WeaponActionIdentity weaponActionIdentity,
            AttackAction action,
            float tpf,
            double gameTimeSeconds
    ) {
        if (weaponActionIdentity == null || weaponActionIdentity.getLastUsedGameTimeSeconds() + weaponActionIdentity.getCooldownSeconds() > gameTimeSeconds) {
            return;
        }

        weaponActionIdentity.setLastUsedGameTimeSeconds(gameTimeSeconds);
        System.err.println("Entity %s attacks with weapon %s - dealing: %s damage. It will be available for use in %s seconds!".formatted(
                        attacker.getId(),
                        weaponEntity.getId(),
                        weaponActionIdentity.getBaseDamage(),
                        weaponActionIdentity.getCooldownSeconds()
                )
        );


        // assume all weaps are raycast for now

        var attackerTransform = entityStore.getComponent(attacker, TransformComponent.class);
        var attackerCameraComponent = entityStore.getComponent(attacker, CameraHolderComponent.class);

        CrosshairSpreadRequestEvent.CrosshairHitmarkerTrigger hitmarkerTrigger = null;

        if (attackerCameraComponent != null && attackerTransform != null) {

            var camOffset = attackerCameraComponent.getCurrentActualCameraOffset();
            var dir = attackerCameraComponent.getCurrentActualCameraRotation().mult(Vector3f.UNIT_Z);

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
                hitmarkerTrigger = new CrosshairSpreadRequestEvent.CrosshairHitmarkerTrigger(false);

                Vector3f normal = entityRaycastResult.getContactNormal().normalize();

                Quaternion rot = new Quaternion();
                rot.lookAt(normal, Vector3f.UNIT_Y);

                vfxPlayer.playEffect(
                        VfxType.BLOOD_HEADSHOT,
                        attackerTransform.getOriginNode().getParent(),
                        entityRaycastResult.getContactPoint().add(entityRaycastResult.getContactNormal().normalize().multLocal(0.05f)),
                        rot);
            }
        }

        var gunViewModelComponent = entityStore.getComponent(weaponEntity, WeaponViewModelComponent.class);

        if (gunViewModelComponent != null) {
            var gunVfx = gunViewModelComponent.getPrimaryActionVfxData();

            if(action.equals(AttackAction.SECONDARY)){
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

        var crosshairSourceComponent = entityStore.getComponent(weaponEntity, CrosshairSourceComponent.class);
        if (crosshairSourceComponent != null) {
            eventBus.publish(new CrosshairSpreadRequestEvent(hitmarkerTrigger), null);

        }
    }

}
