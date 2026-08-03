package com.mccurry.game.gameplay.system.raycast.entityraycast.model;

import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityCallback;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.animation.component.ManualAnimationControlComponent;
import com.mccurry.game.gameplay.system.raycast.RaycastService;
import com.mccurry.game.gameplay.system.raycast.entityraycast.component.EntityRaycastHitboxComponent;
import com.mccurry.game.gameplay.system.raycast.model.RaycastResultSelector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RaycastEntityCallback implements EntityCallback {
    private final RaycastService raycastService;
    private final EntityStore entityStore;

    private final Map<Geometry, EntityRaycastHitbox> hitboxesHitByGeometry = new HashMap<>();
    private final CollisionResults mutableResults = new CollisionResults();
    private final Set<Integer> excludedEntities = new HashSet<>();
    private Ray currentRay;

    private static final Random RANDOM = new Random();

    public RaycastEntityCallback(EntityStore entityStore, RaycastService raycastService) {
        this.entityStore = entityStore;
        this.raycastService = raycastService;
    }

    @Override
    public void execute(Entity entityWithRequiredComponents, float tpf, double currentGameTimeInSeconds) {
        if (excludedEntities.contains(entityWithRequiredComponents.getId())) {
            return;
        }
        var component = entityStore.getComponent(entityWithRequiredComponents, EntityRaycastHitboxComponent.class);
        var bv = component.getBroadphaseSpatial().getWorldBound();
        var intersects = bv.intersects(currentRay);
        if (intersects) {
            for (var hitbox : component.getHitboxes()) {
                var hitboxGeom = hitbox.getGeometry();

                var resultSize = mutableResults.size();
                raycastService.getOneRaycastResult(currentRay, hitboxGeom, RaycastResultSelector.ANY, mutableResults);
                var newResultSize = mutableResults.size();
                if (newResultSize > resultSize) {
                    hitboxesHitByGeometry.put(hitbox.getGeometry(), hitbox);
                }
            }


            var firstHit = mutableResults.getClosestCollision();
            if (firstHit != null) {
                var hitbox = hitboxesHitByGeometry.get(firstHit.getGeometry());
                var hitFeedback = hitbox.getHitFeedback();
                if (hitFeedback != null) {
                    var manualAnimation = entityStore.getComponent(entityWithRequiredComponents, ManualAnimationControlComponent.class);

                    if (manualAnimation != null) {
                        Quaternion currentRotation = hitbox.getGeometry()
                                .getLocalRotation()
                                .clone();

                        float hitImpactStrength = 0f;

                        float weight = hitFeedback.getWeight();
                        if (weight > 1) {
                            hitImpactStrength = 1f / weight;
                        }

                        hitImpactStrength *= RANDOM.nextFloat(0.8f, 1.2f);

// Increase visibility
                        hitImpactStrength *= 3f;
                        hitImpactStrength = FastMath.clamp(hitImpactStrength, 0f, 1f);


// Random hit impulse
                        Quaternion impactRotation = new Quaternion();

                        impactRotation.fromAngles(
                                RANDOM.nextFloat(-0.5f, 0.5f) * hitImpactStrength,
                                RANDOM.nextFloat(-0.5f, 0.5f) * hitImpactStrength,
                                RANDOM.nextFloat(-0.8f, 0.8f) * hitImpactStrength
                        );


// Apply relative rotation
                        Quaternion targetRotation = currentRotation.clone();
                        targetRotation.multLocal(impactRotation);


// Always blend a visible amount
                        Quaternion smootherRotation = currentRotation.clone();
                        smootherRotation.slerp(targetRotation, 0.7f);

                        manualAnimation.addRotationTemporary(hitbox, smootherRotation);
                    }
                }
            }
        }
    }

    public void excludingEntity(int id) {
        excludedEntities.add(id);
    }

    public void checkingAgainstRay(@NotNull Ray ray) {
        this.currentRay = ray;
    }

    public void prepareForNextRaycast() {
        mutableResults.clear();
        excludedEntities.clear();
        hitboxesHitByGeometry.clear();
        currentRay = null;
    }

    public @Nullable EntityRaycastResult acquireResult(RaycastResultSelector selector) {
        if (mutableResults.size() == 0) {
            return null;
        }
        var raycastResult = switch (selector) {
            case ANY -> mutableResults.getCollision(0);
            case CLOSEST -> mutableResults.getClosestCollision();
            case FARTHEST -> mutableResults.getFarthestCollision();
        };

        return new EntityRaycastResult(
                raycastResult.getContactPoint(),
                raycastResult.getContactNormal(),
                raycastResult.getGeometry().getUserData("hitboxOwnerEntityId")
        );
    }
}
