package com.mccurry.game.gameplay.system.attack;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.component.WeaponIdentityComponent;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.GunViewModelComponent;
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
import pocs.SmgCameraRecoilTransformEffect;
import pocs.SmgViewmodelRecoilTransformEffect;

@RequiredArgsConstructor
public class AttackSystem {
    private final MapRaycastSystem mapRaycastSystem;
    private final EntityRaycastSystem entityRaycastSystem;
    private final EventBus eventBus;
    private final EntityStore entityStore;
    private final VfxPlayer vfxPlayer;

    public void processAttack(Entity attacker, Entity weaponEntity, WeaponIdentityComponent weaponIdentityComponent, float tpf, double gameTimeSeconds) {
        if (weaponIdentityComponent.getLastUsedGameTimeSeconds() + weaponIdentityComponent.getCooldownSeconds() > gameTimeSeconds) {
            return;
        }

        weaponIdentityComponent.setLastUsedGameTimeSeconds(gameTimeSeconds);
        System.err.println("Entity %s attacks with weapon %s - dealing: %s damage. It will be available for use in %s seconds!".formatted(
                        attacker.getId(),
                        weaponEntity.getId(),
                        weaponIdentityComponent.getBaseDamage(),
                        weaponIdentityComponent.getCooldownSeconds()
                )
        );


        // assume all weaps are raycast for now

        var attackerTransform = entityStore.getComponent(attacker, TransformComponent.class);
        var attackerCameraComponent = entityStore.getComponent(attacker, CameraHolderComponent.class);
        if(attackerCameraComponent != null && attackerTransform != null){

            var camOffset = attackerCameraComponent.getCurrentActualCameraOffset();
            var dir = attackerCameraComponent.getCurrentActualCameraRotation().mult(Vector3f.UNIT_Z);

            var camPos = attackerTransform.getOriginNode().getLocalTranslation().clone().add(camOffset);

            var mapRaycastResult = mapRaycastSystem.getOneRaycastResult(camPos, dir, RaycastResultSelector.CLOSEST);
            var entityRaycastResult = entityRaycastSystem.getOneRaycastResult(attacker.getId() ,camPos, dir, RaycastResultSelector.CLOSEST, tpf, gameTimeSeconds);

            boolean isMapCollisionValid = false;
            boolean isEntityCollisionValid = false;

            if(mapRaycastResult != null){
                isMapCollisionValid = true;
            }

            if(entityRaycastResult != null){
                isEntityCollisionValid = true;
            }

            if(isEntityCollisionValid && isMapCollisionValid){
                   var mapCollisionDistance = camPos.distance(mapRaycastResult.getContactPoint());
                   var entityCollisionDistance = camPos.distance(entityRaycastResult.getContactPoint());
                   isMapCollisionValid = mapCollisionDistance < entityCollisionDistance;
                   isEntityCollisionValid = !isMapCollisionValid;
            }

            if(isMapCollisionValid){
                Vector3f normal = mapRaycastResult.getContactNormal().normalize();

                Quaternion rot = new Quaternion();
                rot.lookAt(normal, Vector3f.UNIT_Y);

                vfxPlayer.playEffect(
                        VfxType.SMG_BULLET_HIT,
                        attackerTransform.getOriginNode().getParent(),
                        mapRaycastResult.getContactPoint().add(mapRaycastResult.getContactNormal().normalize().multLocal(0.05f)),
                        rot);
            }

            if(isEntityCollisionValid){
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

        var gunViewModelComponent = entityStore.getComponent(weaponEntity, GunViewModelComponent.class);

        if(gunViewModelComponent != null){
            var gunVfx = gunViewModelComponent.getGunVfxData();
            if(gunVfx != null && gunViewModelComponent.getMuzzleNode() != null){
                vfxPlayer.playEffect(gunVfx.getMuzzleFlash(), gunViewModelComponent.getMuzzleNode(), gunVfx.getMuzzleFlashOffset(), Quaternion.IDENTITY.clone());
            }

            if(gunVfx != null && gunViewModelComponent.getBulletCaseEjectionNode() != null){
                vfxPlayer.playEffect(gunVfx.getCaseEjection(), gunViewModelComponent.getBulletCaseEjectionNode(), new Vector3f(0,0,0), Quaternion.IDENTITY.clone());
            }

            var animData = gunViewModelComponent.getGunAnimationData();
            var animComposer = gunViewModelComponent.getAnimComposer();
            if(animData != null && animComposer != null){
                var recoilAnim = animData.getLockRecoilAnimName();
                if(recoilAnim != null && animComposer.getAnimClipsNames().contains(recoilAnim)){
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
        if(cameraEffectsComponent != null) {
            var cameraRecoil = new SmgCameraRecoilTransformEffect();
            cameraRecoil.setEnabled(true);
            cameraRecoil.onShot();
            cameraEffectsComponent.addMultiInstanceEffect(
                    cameraRecoil
            );
        }

        var crosshairSourceComponent = entityStore.getComponent(weaponEntity, CrosshairSourceComponent.class);
        if(crosshairSourceComponent != null){
            eventBus.publish(new CrosshairSpreadRequestEvent(), null);

        }
    }

}
