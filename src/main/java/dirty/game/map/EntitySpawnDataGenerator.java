package dirty.game.map;

import dirty.LevelLoadSystem.entitySpawnData.EntitySpawnData;
import dirty.LevelLoadSystem.entitySpawnData.IndestructibleDecorationSpawnData;
import dirty.LevelLoadSystem.entitySpawnData.ItemSpawnData;
import dirty.LevelLoadSystem.entitySpawnData.MobSpawnData;
import com.jme3.math.Vector3f;
import data.ChanceEntry;
import dirty.game.entities.factories.MobSpawnType;
import dirty.itemSystem.ItemTemplates;
import dirty.game.map.proceduralGeneration.Room;
import generators.PercentageRandomGenerator;

import jme3utilities.math.Vector3i;
import dirty.server.ServerGameAppState;

import java.util.*;

public class EntitySpawnDataGenerator {
    private final long seed;
    private final int playerCount = ServerGameAppState.MAX_PLAYERS;
    private static final PercentageRandomGenerator<MobSpawnType> mobSpawnTypeRandomGenerator;
    private static final PercentageRandomGenerator<ItemTemplates.ItemTemplate> itemTemplateRandomGenerator;

    private static final List<ChanceEntry<MobSpawnType>> mobsByChance = new ArrayList<>();
    private static final List<ChanceEntry<ItemTemplates.ItemTemplate>> itemsByChance = new ArrayList<>();
    static {
        mobsByChance.add(new ChanceEntry<>(40f, MobSpawnType.SOLDIER));
        mobsByChance.add(new ChanceEntry<>(59.5f, MobSpawnType.MUD_BEETLE));
        mobsByChance.add(new ChanceEntry<>(0.25f, MobSpawnType.RED_HAND_1));
        mobsByChance.add(new ChanceEntry<>(0.25f, MobSpawnType.TRAINING_DUMMY));

        itemsByChance.add(new ChanceEntry<>(7.75f, ItemTemplates.PISTOL_AMMO_PACK));
        itemsByChance.add(new ChanceEntry<>(5f, ItemTemplates.MEDPACK));
        itemsByChance.add(new ChanceEntry<>(7f, ItemTemplates.RIFLE_AMMO_PACK));
        itemsByChance.add(new ChanceEntry<>(5f, ItemTemplates.PISTOL_C96));
        itemsByChance.add(new ChanceEntry<>(1.75f, ItemTemplates.RIFLE_BORYSOV));
        itemsByChance.add(new ChanceEntry<>(2f, ItemTemplates.RIFLE_MANNLICHER_95));
        itemsByChance.add(new ChanceEntry<>(1f,ItemTemplates.LMG_HOTCHKISS));

        itemsByChance.add(new ChanceEntry<>(2.5f,ItemTemplates.LMG_AMMO_PACK));

        itemsByChance.add(new ChanceEntry<>(3f,ItemTemplates.GAS_MASK));
        itemsByChance.add(new ChanceEntry<>(5f, ItemTemplates.VEST_TRENCH));
        itemsByChance.add(new ChanceEntry<>(5f, ItemTemplates.BOOTS_TRENCH));
        itemsByChance.add(new ChanceEntry<>(3f, ItemTemplates.TRENCH_HELMET));

        itemsByChance.add(new ChanceEntry<>(37.25f, ItemTemplates.KNIFE));
        itemsByChance.add(new ChanceEntry<>(13.75f, ItemTemplates.AXE));
        itemsByChance.add(new ChanceEntry<>(1f, ItemTemplates.ARTIFACT_CORNUCOPIA));

        mobSpawnTypeRandomGenerator = new PercentageRandomGenerator<>(mobsByChance);
        itemTemplateRandomGenerator = new PercentageRandomGenerator<>(itemsByChance);
    }

    public EntitySpawnDataGenerator(long seed){
        this.seed = seed;
    }

    public List<EntitySpawnData> generateEntitySpawnData(MapGenerationResult mapGenerationResult){
        var random = new Random(seed);
        var entitySpawnData = new ArrayList<EntitySpawnData>();
        var minItemsPerRoom = 0;
        var maxItemsPerRoom = 3;

        var minMobsPerRoom = 1;
        var maxMobsPerRoom = 4;
        var exitNotCreated = true;


        if(mapGenerationResult.getRooms()==null){

            while(exitNotCreated) {
                int x = random.nextInt(mapGenerationResult.getMap().getMapSizeX());
                int z = random.nextInt(mapGenerationResult.getMap().getMapSizeZ());
                if(mapGenerationResult.getMap().getBlockIdAtPosition(x,1,z)!=0){
                    continue;
                }
                if (exitNotCreated) {
                    var exitSpawnData = new IndestructibleDecorationSpawnData(3,
                            new Vector3f(
                                    x + 0.5f,
                                    1,
                                    z+ 0.5f
                            ));
                    entitySpawnData.add(exitSpawnData);
                    exitNotCreated = false;
                }
            }


            return entitySpawnData;
        }

        var rooms = mapGenerationResult.getRooms();
        var startingRoom = getStartingRoom(rooms);

        for(int roomIndex = 0; roomIndex < rooms.size(); roomIndex++){
            var room = rooms.get(roomIndex);

            if(room.equals(startingRoom)){
                continue;
            }
            if(exitNotCreated){
                var exitSpawnData = new IndestructibleDecorationSpawnData(3,
                        new Vector3f(
                                random.nextInt(room.getStartX(),room.getEndX())+0.5f,
                                room.getStartY()+1,
                                random.nextInt(room.getStartZ(),room.getEndZ())+0.5f
                        ));
                entitySpawnData.add(exitSpawnData);
                exitNotCreated=false;
            }

            var actualAmountOfMobsInRoom = random.nextInt(minMobsPerRoom,maxMobsPerRoom+1);
            for(int i = 0; i < actualAmountOfMobsInRoom; i++) {
                var mobSpawnData = new MobSpawnData(mobSpawnTypeRandomGenerator.getRandom(), new Vector3f(
                        random.nextInt(room.getStartX(), room.getEndX()) + 0.5f,
                        room.getStartY() + 1,
                        random.nextInt(room.getStartZ(), room.getEndZ()) + 0.5f
                ));
                entitySpawnData.add(mobSpawnData);
            }


            var actualAmountOfItems = random.nextInt(minItemsPerRoom,maxItemsPerRoom+1);
            for(int i = 0; i < actualAmountOfItems; i++) {
                var mobSpawnData = new ItemSpawnData(itemTemplateRandomGenerator.getRandom().getTemplateIndex(), new Vector3f(
                        random.nextInt(room.getStartX(), room.getEndX()) + 0.5f,
                        room.getStartY() + 1,
                        random.nextInt(room.getStartZ(), room.getEndZ()) + 0.5f
                ),true, true);
                entitySpawnData.add(mobSpawnData);
            }

        }



        return entitySpawnData;
    }

    public List<Vector3f> generatePlayerSpawnpoints(MapGenerationResult mapGenerationResult){
        List<Vector3f> playerSpawnpoints = new ArrayList<>();
        var random = new Random(seed);


        if(mapGenerationResult.getRooms() == null){
            Vector3i randomCell;
            while(true){
                int x = random.nextInt(0,mapGenerationResult.getMap().getMapSizeX());
                int y = 1;
                int z = random.nextInt(0,mapGenerationResult.getMap().getMapSizeZ());
                if(mapGenerationResult.getMap().getBlockIdAtPosition(x,y,z) == 0){
                    randomCell = new Vector3i(x,y,z);
                    break;
                }
            }
            while (playerSpawnpoints.size() < playerCount) {
                var inCellOffsetX = random.nextFloat() <0.5f ? 0.2f : 0.8f;
                var inCellOffsetZ = random.nextFloat() <0.5f ? 0.2f : 0.8f;
                var playerSpawnpoint = new Vector3f(
                        randomCell.x() + inCellOffsetX,
                        1,
                        randomCell.z() + inCellOffsetZ
                );
                if (!playerSpawnpoints.contains(playerSpawnpoint)) {
                    playerSpawnpoints.add(playerSpawnpoint);
                }
            }

            return playerSpawnpoints;
        }





        var startingRoom = getStartingRoom(mapGenerationResult.getRooms());


        while (playerSpawnpoints.size() < playerCount){
            var playerSpawnpoint = new Vector3f(
                    random.nextInt(startingRoom.getStartX(), startingRoom.getEndX())+0.5f,
                    startingRoom.getStartY()+1,
                    random.nextInt(startingRoom.getStartZ(), startingRoom.getEndZ())+0.5f
            );
            if(!playerSpawnpoints.contains(playerSpawnpoint)){
                playerSpawnpoints.add(playerSpawnpoint);
            }
        }
        return playerSpawnpoints;
    }

    public Room getStartingRoom(List<Room> rooms){
        var random = new Random(seed);
        var startingRoomIndex = random.nextInt(rooms.size());
        return rooms.get(startingRoomIndex);
    }
}
