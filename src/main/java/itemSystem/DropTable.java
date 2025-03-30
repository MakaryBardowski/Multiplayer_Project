package itemSystem;

import data.ChanceEntry;
import generators.PercentageRandomGenerator;
import generators.RandomGenerator;

import java.util.Arrays;

public class DropTable<T>{
    private RandomGenerator<T> randomGen;

    public DropTable(ChanceEntry<T>... drops){
        randomGen = new PercentageRandomGenerator<>(Arrays.asList(drops));
    }

    public T getDrop(){
        return randomGen.getRandom();
    }
}
