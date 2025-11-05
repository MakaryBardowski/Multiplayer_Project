package dirty.LevelLoadSystem.entitySpawnData.entityPrototypeData;

import dirty.LevelLoadSystem.entitySpawnData.EntitySpawnData;

public abstract class EntitySpawnDataSelector<T extends EntitySpawnData> extends EntitySpawnDataPrototype<T>{
    public EntitySpawnDataSelector(int localResultReferenceId) {
        super(localResultReferenceId);
    }
}
