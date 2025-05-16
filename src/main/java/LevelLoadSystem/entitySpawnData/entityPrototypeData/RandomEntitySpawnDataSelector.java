package LevelLoadSystem.entitySpawnData.entityPrototypeData;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import generators.RandomGenerator;


public class RandomEntitySpawnDataSelector<T extends EntitySpawnData> extends EntitySpawnDataSelector<T> {
    private final RandomGenerator<T> randomGenerator;

    public RandomEntitySpawnDataSelector(int resultReferenceId, RandomGenerator<T> randomGenerator) {
        super(resultReferenceId);
        this.randomGenerator = randomGenerator;
    }

    @Override
    public T resolve() {
        return randomGenerator.getRandom();
    }
}
