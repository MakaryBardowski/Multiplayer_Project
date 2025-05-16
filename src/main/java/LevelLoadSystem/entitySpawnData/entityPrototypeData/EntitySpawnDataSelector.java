package LevelLoadSystem.entitySpawnData.entityPrototypeData;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;

public abstract class EntitySpawnDataSelector<T extends EntitySpawnData> extends EntitySpawnDataPrototype<T>{
    public EntitySpawnDataSelector(int localResultReferenceId) {
        super(localResultReferenceId);
    }
}
