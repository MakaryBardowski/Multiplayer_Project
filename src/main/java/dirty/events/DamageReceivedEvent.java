package dirty.events;

import dirty.data.DamageReceiveData;
import lombok.Getter;
import lombok.Setter;

public class DamageReceivedEvent extends GameEvent {
    @Getter
    @Setter
    private DamageReceiveData damageData;
    public DamageReceivedEvent(DamageReceiveData damageData){
        this.damageData = damageData;
    }
}
