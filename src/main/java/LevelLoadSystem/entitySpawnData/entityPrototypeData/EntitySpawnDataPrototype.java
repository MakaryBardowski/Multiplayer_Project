package LevelLoadSystem.entitySpawnData.entityPrototypeData;


import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class EntitySpawnDataPrototype<T extends EntitySpawnData> {
    protected final int localResultReferenceId;
    public abstract T resolve();
}
