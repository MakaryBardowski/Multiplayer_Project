package com.mccurry.game.gameplay.system.equipment;

import com.mccurry.game.gameplay.core.ecs.component.Component;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.system.equipment.model.ArmorSlot;
import com.mccurry.game.gameplay.system.equipment.model.HandSlot;

import java.util.HashMap;
import java.util.Map;

public class EquipmentComponent implements Component {
    private final Map<HandSlot, Entity> toolPerSlot = new HashMap<>();
    private final Map<ArmorSlot, Entity> armorsPerSlot = new HashMap<>();

    public Entity getTool(HandSlot handSlot){
        return toolPerSlot.get(handSlot);
    }

    private Entity getArmor(ArmorSlot armorSlot){
        return armorsPerSlot.get(armorSlot);
    }

    public void putTool(HandSlot handSlot, Entity entity){
        toolPerSlot.put(handSlot, entity);
    }

    private void putArmor(ArmorSlot armorSlot, Entity entity){
        armorsPerSlot.put(armorSlot,entity);
    }
}
