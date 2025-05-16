package statusEffects.onEnterLevelEffects;

import data.ChanceEntry;
import game.entities.StatusEffectContainer;
import game.entities.mobs.Mob;
import itemSystem.DropTable;
import itemSystem.ItemTemplates;
import messages.TwoWayMessage;
import messages.items.MobItemInteractionMessage;
import server.ServerGameAppState;
import statusEffects.EffectProcType;

public class CornucopiaEffect extends OnEnterLevelEffect{
    private DropTable<ItemTemplates.ItemTemplate> dropTable = new DropTable<>(
            new ChanceEntry<>(30,ItemTemplates.MEDPACK),
            new ChanceEntry<>(14.95f,ItemTemplates.PISTOL_AMMO_PACK),
            new ChanceEntry<>(14.95f,ItemTemplates.RIFLE_AMMO_PACK),
            new ChanceEntry<>(10,ItemTemplates.LMG_HOTCHKISS),
            new ChanceEntry<>(10,ItemTemplates.RIFLE_BORYSOV),
            new ChanceEntry<>(10,ItemTemplates.PISTOL_C96),
            new ChanceEntry<>(10,ItemTemplates.LMG_AMMO_PACK),
            new ChanceEntry<>(0.1f,ItemTemplates.ARTIFACT_CORNUCOPIA)
            );
    public CornucopiaEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public Void applyServer(Void input) {
        if(target != null && target instanceof Mob mobTarget){
            var cornucopiaItem = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager().registerItemLocal(dropTable.getDrop(),true);
            mobTarget.getEquipment().addItem(cornucopiaItem);


            var newItemMsg = cornucopiaItem.createNewEntityMessage();
            newItemMsg.setReliable(true);
            ServerGameAppState.getInstance().getServer().broadcast(newItemMsg);


            MobItemInteractionMessage msg = new MobItemInteractionMessage(cornucopiaItem, mobTarget, MobItemInteractionMessage.ItemInteractionType.PICK_UP);
            ServerGameAppState.getInstance().getServer().broadcast(msg);
        }
        return null;
    }

    @Override
    public Void applyClient(Void input) {
        return null;
    }

    @Override
    public boolean shouldBeRemoved() {
        return false;
    }

    @Override
    public TwoWayMessage createNewEntityMessage() {
        return null;
    }
}
