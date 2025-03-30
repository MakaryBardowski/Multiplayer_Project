package events;

import data.DamageReceiveData;
import lombok.Getter;

public class NewLevelEnteredEvent extends GameEvent {
    @Getter
    private final int levelIndex;
    public NewLevelEnteredEvent(int levelIndex){
        this.levelIndex = levelIndex;
    }
}
