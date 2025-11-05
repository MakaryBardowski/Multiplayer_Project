package dirty.LevelLoadSystem.entitySpawnData.entityPrototypeData;


import dirty.LevelLoadSystem.entitySpawnData.EntitySpawnData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class EntitySpawnDataPrototype<T extends EntitySpawnData> {
    protected final int localResultReferenceId;
    public abstract T resolve();
}
