package com.mccurry.game.gameplay.system.attack;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.attack.component.WeaponIdentityComponent;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.GunViewModelComponent;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.FirstPersonViewTransformEffectsComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraTransformEffectsComponent;
import com.mccurry.game.shared.sfx.player.VfxPlayer;
import lombok.RequiredArgsConstructor;
import pocs.SmgCameraRecoilTransformEffect;
import pocs.SmgViewmodelRecoilTransformEffect;

@RequiredArgsConstructor
public class AttackSystem {
    private final EntityStore entityStore;
    private final VfxPlayer vfxPlayer;

    public void processAttack(Entity attacker, Entity weaponEntity, WeaponIdentityComponent weaponIdentityComponent, double gameTimeSeconds) {
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
    }

}
