package dirty.game.items.factories;

import dirty.client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import dirty.game.items.AmmoPack;
import dirty.game.items.Item;
import dirty.itemSystem.ItemTemplates;
import dirty.itemSystem.ItemTemplates.HelmetTemplate;
import dirty.itemSystem.ItemTemplates.ItemTemplate;
import dirty.itemSystem.ItemTemplates.VestTemplate;
import dirty.itemSystem.ItemTemplates.BootsTemplate;
import dirty.itemSystem.ItemTemplates.GlovesTemplate;
import dirty.itemSystem.ItemTemplates.MeleeWeaponTemplate;
import dirty.itemSystem.ItemTemplates.RangedWeaponTemplate;
import dirty.game.items.armor.Boots;
import dirty.game.items.armor.Gloves;
import dirty.game.items.armor.Helmet;
import dirty.game.items.armor.Vest;
import dirty.game.items.consumable.Medpack;
import dirty.game.items.misc.MiscellaneousItem;
import dirty.game.items.weapons.Axe;
import dirty.game.items.weapons.Grenade;
import dirty.game.items.weapons.Knife;
import dirty.game.items.weapons.LightMachineGun;
import dirty.game.items.weapons.Pistol;
import dirty.game.items.weapons.Rifle;

public class ItemFactory {

    private AssetManager assetManager;

    public ItemFactory() {
        this.assetManager = Main.getInstance().getAssetManager();
    }

    public Item createItem(int id, ItemTemplate template, boolean droppable) {
        if (null != template.getType()) {
            Item item = null;
            switch (template.getType()) {
                case HELMET:
                    item = createHelmet(id, (HelmetTemplate) template, droppable);
                    break;
                case VEST:
                    item = createVest(id, (VestTemplate) template, droppable);
                    break;
                case GLOVES:
                    item =  createGloves(id, (GlovesTemplate) template, droppable);
                    break;
                case BOOTS:
                    item = createBoots(id, (BootsTemplate) template, droppable);
                    break;
                case RIFLE:
                    item = createRifle(id, (RangedWeaponTemplate) template, droppable);
                    break;
                case PISTOL:
                    item = createPistol(id, (RangedWeaponTemplate)template, droppable);
                    break;
                case LMG:
                    item = createLmg(id, (RangedWeaponTemplate)template, droppable);
                    break;
                case GRENADE:
                    item = createGrenade(id, template, droppable);
                    break;
                case KNIFE:
                    item = createKnife(id, (MeleeWeaponTemplate)template, droppable);
                    break;
                case AXE:
                    item =  createAxe(id, (MeleeWeaponTemplate)template, droppable);
                    break;
                case PISTOL_AMMO:
                    item = createPistolAmmoPack(id, template, droppable);
                    break;
                case RIFLE_AMMO:
                    item =  createRifleAmmoPack(id, template, droppable);
                    break;
                case SMG_AMMO:
                    item = createSmgAmmoPack(id, template, droppable);
                    break;
                case LMG_AMMO:
                    item = createLmgAmmoPack(id, template, droppable);
                    break;
                case SHOTGUN_AMMO:
                    item = createShotgunAmmoPack(id, template, droppable);
                    break;
                case MEDPACK:
                    item = createMedpack(id, template, droppable);
                    break;
                case REPORT:
                    item = createReport(id,template,droppable);
                    break;
                case CORNUCOPIA:
                    item = createCornucopia(id,template,droppable);
                    break;
                default:
                    break;
            }
            item.setServerGameEventHandler(template.getServerGameEventHandler());
            return item;
        }
        return null;
    }

    private Helmet createHelmet(int id, HelmetTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Helmet helmet = new Helmet(id, template, template.getName(), dropNode, droppable);
        return helmet;
    }

    private Vest createVest(int id, VestTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Vest vest = new Vest(id, template, template.getName(), dropNode, droppable);
        return vest;
    }

    private Gloves createGloves(int id, GlovesTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Gloves gloves = new Gloves(id, template, template.getName(), dropNode, droppable);
        return gloves;
    }

    private Boots createBoots(int id, BootsTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Boots boots = new Boots(id, template, template.getName(), dropNode, droppable);
        return boots;
    }

    private Rifle createRifle(int id, RangedWeaponTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Rifle rifle = new Rifle(id, template.getDefaultStats().getDamage(), template, template.getName(), dropNode, droppable, template.getDefaultStats().getMaxAmmo(), template.getDefaultStats().getAttacksPerSecond());
        return rifle;
    }

    private LightMachineGun createLmg(int id, RangedWeaponTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        LightMachineGun rifle = new LightMachineGun(id, template.getDefaultStats().getDamage(), template, template.getName(), dropNode, droppable, template.getDefaultStats().getMaxAmmo(), template.getDefaultStats().getAttacksPerSecond());
        return rifle;
    }

    private Item createPistol(int id, RangedWeaponTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Pistol pistol = new Pistol(id, template.getDefaultStats().getDamage(), template, template.getName(), dropNode, droppable, template.getDefaultStats().getMaxAmmo(), template.getDefaultStats().getAttacksPerSecond());
        return pistol;
    }

    private Item createGrenade(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Grenade grenade = new Grenade(id, 0f, template, "Smoke Grenade", dropNode, droppable);
        return grenade;
    }

    private Item createKnife(int id, MeleeWeaponTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Knife pistol = new Knife(id, template.getDefaultStats().getDamage(), template, template.getName(), dropNode, droppable, template.getDefaultStats().getAttacksPerSecond());
        return pistol;
    }

    private Item createAxe(int id, MeleeWeaponTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Axe pistol = new Axe(id, template.getDefaultStats().getDamage(), template, template.getName(), dropNode, droppable, template.getDefaultStats().getAttacksPerSecond());
        return pistol;
    }

    private Item createPistolAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 24;
        short maxAmmo = 24;
        AmmoPack ammoPack = new AmmoPack(id, template, template.getName(), dropNode, droppable, ammo, maxAmmo);
        return ammoPack;
    }

    private Item createRifleAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 23;
        short maxAmmo = 23;
        AmmoPack ammoPack = new AmmoPack(id, template, template.getName(), dropNode, droppable, ammo, maxAmmo);
        return ammoPack;
    }

    private Item createSmgAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 24;
        short maxAmmo = 24;
        AmmoPack ammoPack = new AmmoPack(id, template, template.getName(), dropNode, droppable, ammo, maxAmmo);
        return ammoPack;
    }

    private Item createLmgAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 45;
        short maxAmmo = 45;
        AmmoPack ammoPack = new AmmoPack(id, template, template.getName(), dropNode, droppable, ammo, maxAmmo);
        return ammoPack;
    }

    private Item createShotgunAmmoPack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        short ammo = 9;
        short maxAmmo = 9;
        AmmoPack ammoPack = new AmmoPack(id, template, template.getName(), dropNode, droppable, ammo, maxAmmo);
        return ammoPack;
    }

    private Item createMedpack(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        Medpack medpack = new Medpack(id, 0f, template, template.getName(), dropNode, droppable);
        return medpack;
    }

    private Item createReport(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        MiscellaneousItem report = new MiscellaneousItem(id, template, template.getName(), dropNode, droppable);
        return report;
    }

    private Item createCornucopia(int id, ItemTemplate template, boolean droppable) {
        Node dropNode = createItemDropNode(template);
        return new MiscellaneousItem(id, template, template.getName(), dropNode, droppable);
    }

    /**
     *
     * @param template the visual template for the item (including its FP
     * model,drop model and drop offsets)
     * @return Node - being the drop model based on provided template
     */
    private Node createItemDropNode(ItemTemplate template) {
        if (assetManager == null) {
            return new Node();
        }
        if (template.getDropPath() != null) {
            Node dropNode = (Node) assetManager.loadModel(template.getDropPath());
            return dropNode;
        } else {
            Node dropNode = (Node) assetManager.loadModel(ItemTemplates.RIFLE_MANNLICHER_95.getDropPath());
            return dropNode;
        }
    }

}
