package com.mccurry.game.gameplay.system.equipment.core.component;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.system.equipment.core.model.BodySlot;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;

import java.util.HashMap;
import java.util.Map;

public class EquipmentComponent implements Component {
    private final Map<HandSlot, Entity> toolPerSlot = new HashMap<>();
    private final Map<BodySlot, Entity> armorsPerSlot = new HashMap<>();

    public Entity getWorn(HandSlot handSlot){
        return toolPerSlot.get(handSlot);
    }
    public void setWorn(HandSlot handSlot, Entity entity){
        toolPerSlot.put(handSlot, entity);
    }


    private Entity getWorn(BodySlot bodySlot){
        return armorsPerSlot.get(bodySlot);
    }
    private void setWorn(BodySlot bodySlot, Entity entity){
        armorsPerSlot.put(bodySlot,entity);
    }
}
