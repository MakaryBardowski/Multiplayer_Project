package com.mccurry.game.gameplay.core;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.console.command.spectator.ToggleSpectatorModeEventHandler;
import com.mccurry.game.console.event.ToggleRotationDebugEvent;
import com.mccurry.game.console.event.ToggleSpectatorModeEvent;
import com.mccurry.game.console.model.GameContext;
import com.mccurry.game.constants.CameraConstants;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityCallback;
import com.mccurry.game.gameplay.core.ecs.system.EntitySystem;
import com.mccurry.game.gameplay.system.attack.AttackSystem;
import com.mccurry.game.gameplay.system.attack.component.WeaponComponent;
import com.mccurry.game.gameplay.system.equipment.core.EquipSystem;
import com.mccurry.game.gameplay.system.equipment.visual.VisualEquipSystem;
import com.mccurry.game.gameplay.system.equipment.core.component.EquipmentComponent;
import com.mccurry.game.gameplay.system.equipment.core.component.EquippableComponent;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;
import com.mccurry.game.gameplay.system.intent.event.HandEquipIntentEvent;
import com.mccurry.game.gameplay.system.intent.resolver.model.IntentSystems;
import com.mccurry.game.gameplay.system.intent.system.IntentSystem;
import com.mccurry.game.gameplay.system.modelhierarchy.component.HumanoidModelComponent;
import com.mccurry.game.gameplay.system.movement.MovementEntitySystem;
import com.mccurry.game.gameplay.system.movement.component.VelocityComponent;
import com.mccurry.game.gameplay.system.player.CameraEntitySystem;
import com.mccurry.game.gameplay.system.player.PlayerInputSystem;
import com.mccurry.game.gameplay.system.player.PlayerRotationControlSystem;
import com.mccurry.game.gameplay.system.player.component.camera.CameraEffectsComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.player.component.control.PlayerControlledComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.keyBinding.KeyMappingRegistry;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.util.debug.GeometryUtil;
import com.mccurry.game.util.event.EventHandlers;
import com.mccurry.map.model.BlockType;
import com.mccurry.map.model.Map;
import com.mccurry.map.visuals.BlockWorld;
import com.simsilica.lemur.GuiGlobals;

import java.util.ArrayList;
import java.util.List;

import static com.mccurry.game.keyBinding.KeyMappingRegistry.*;
import static com.mccurry.game.util.debug.GeometryUtil.createUnshadedBox;

public class GameState extends BaseAppState {
    private double gameTimeInSeconds = 0;
    private EntityStore entityStore = new EntityStore();

    private final List<EntitySystem> entitySystems = new ArrayList<>();

    private PlayerInputSystem playerInputSystem;
    private PlayerRotationControlSystem playerRotationControlSystem;
    private IntentSystem intentSystem;
    private VisualEquipSystem visualEquipSystem;

    private AttackSystem attackSystem;
    private EquipSystem equipSystem;

    private final EventBus gameEventBus = new EventBus();

    private ToggleSpectatorModeEventHandler spectatorModeEventHandler;
    private SyncEventHandler<ToggleRotationDebugEvent> onRotationDebugEnable = EventHandlers.syncEventHandler(
            (event, elm) -> {
                entityStore.forEachEntityWithComponents(0, gameTimeInSeconds,new EntityCallback() {
                    @Override
                    public void execute(Entity entityWithRequiredComponents, float tpf, double currentGameTimeInSeconds) {

                        var transform = entityStore.getComponent(entityWithRequiredComponents, TransformComponent.class);
                        transform.getOriginNode().attachChild(
                                GeometryUtil.createArrow(2, Vector3f.UNIT_Z, ColorRGBA.Red, getApplication().getAssetManager())
                        );

                        transform.getVisualNode().attachChild(
                                GeometryUtil.createArrow(4, Vector3f.UNIT_Z, ColorRGBA.Blue, getApplication().getAssetManager())
                        );
                    }
                }, TransformComponent.class);
            }
    );


    @Override
    protected void initialize(Application app) {
        var assetManager = getApplication().getAssetManager();
        entitySystems.add(new MovementEntitySystem(entityStore));
        entitySystems.add(new CameraEntitySystem(entityStore,app.getCamera()));



        playerInputSystem = new PlayerInputSystem(entityStore,gameEventBus);
        playerRotationControlSystem = new PlayerRotationControlSystem(entityStore);

        attackSystem = new AttackSystem(entityStore);
        equipSystem = new EquipSystem(gameEventBus,entityStore);
        visualEquipSystem = new VisualEquipSystem(entityStore);
        intentSystem = new IntentSystem(
                new IntentSystems(attackSystem, equipSystem),
                entityStore
        );


        getApplication().getInputManager().addListener(playerInputSystem,
                KeyMappingRegistry.MAPPING_KEY_W,
                MAPPING_KEY_S,
                MAPPING_KEY_A,
                MAPPING_KEY_D,
                MAPPING_MOUSE_LEFT
        );

        getApplication().getInputManager().addListener(playerRotationControlSystem,
                MAPPING_MOUSE_MOVE_X_LEFT,
                MAPPING_MOUSE_MOVE_X_RIGHT,
                MAPPING_MOUSE_MOVE_Y_DOWN,
                MAPPING_MOUSE_MOVE_Y_UP
        );

        var universeNode = new Node();
        var blockWorldNode = new Node();
        int mapX = 40;
        int mapY = 10;
        int mapZ = 40;
        var lm = new byte[mapX * mapY * mapZ];
        var m = new Map(lm, mapX, mapY, mapZ, 3);

        for (int x = 0; x < mapX; x++) {
            for (int z = 0; z < mapZ; z++) {
                m.setBlockIdAtPosition(x, 0, z, BlockType.DIRT.blockId);
            }
        }
        m.setBlockIdAtPosition(1,0,1, BlockType.STONE.blockId);
        m.setBlockIdAtPosition(0,0,1, BlockType.STONE.blockId);
        m.setBlockIdAtPosition(1,0,0, BlockType.STONE.blockId);

        var b = new BlockWorld(3, 16, mapX, mapY, mapZ, m, app.getAssetManager(), blockWorldNode);



        universeNode.attachChild(blockWorldNode);
        ((SimpleApplication) getApplication()).getRootNode().attachChild(universeNode);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.5f));
        universeNode.addLight(al);

        var weaponEntity = entityStore.create(
                new WeaponComponent(10f,0.5f, 0),
                EquippableComponent.builder()
                        .handSlot(HandSlot.HAND_RIGHT)
                        .displayNode(setupModelLight((Node) assetManager.loadModel("Models/mp18_tp/mp18_tp.j3o")))
                        .build()
        );



        var humanoidRootNode = setupModelLight((Node) getApplication().getAssetManager().loadModel("Models/HumanArmature/HumanArmature.j3o"));

        var transformComponent = new TransformComponent();
        var humanoidModelComponent = new HumanoidModelComponent(humanoidRootNode);
        transformComponent.getVisualNode().attachChild(humanoidRootNode);
        universeNode.attachChild(transformComponent.getOriginNode());
        transformComponent.getOriginNode().move(0,3,0);
        var velocityComponent = new VelocityComponent();
        var playerControlled = new PlayerControlledComponent();
        var cameraHolder = new CameraHolderComponent(new Vector3f(0,2.067f,0));
        var cameraEffects = new CameraEffectsComponent();
        var equipmentComponent = new EquipmentComponent();

        var player = entityStore.create(
                transformComponent,
                humanoidModelComponent, // required for visual equipping
                velocityComponent, // can move
                playerControlled, // player controlled
                cameraHolder,  // camera is glued to this player
                cameraEffects, // can have camera effects
                equipmentComponent
        );




        spectatorModeEventHandler = new ToggleSpectatorModeEventHandler(entityStore);

        subscribeSystems();
        gameEventBus.publish(new HandEquipIntentEvent(player, weaponEntity, HandSlot.HAND_RIGHT),null);
    }

    @Override
    public void update(float tpf) {
        long t1 = System.nanoTime();




        gameTimeInSeconds += tpf;
        intentSystem.processQueued(tpf, gameTimeInSeconds);


        for (var system : entitySystems) {
            system.processEligibleEntities(tpf, gameTimeInSeconds);
        }






        long t2 = System.nanoTime();
        long res = t2 - t1;
        double s = (double) res / 1000000000;
//        System.out.printf("%.9f s%n", s);
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        var consoleLauncher = getApplication().getStateManager().getState(GameStateOrchestrator.class).getCheatConsoleLauncher();
        if(consoleLauncher != null){
            consoleLauncher.getCheatConsoleState().setGameContext(new GameContext(gameEventBus));
        }

        getApplication().getViewPort().setBackgroundColor(ColorRGBA.Cyan);
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
        subscribeSystems();
    }

    @Override
    protected void onDisable() {
        var consoleLauncher = getApplication().getStateManager().getState(GameStateOrchestrator.class).getCheatConsoleLauncher();
        if(consoleLauncher != null){
            consoleLauncher.getCheatConsoleState().setGameContext(null);
        }


        CameraConstants.applyDefaultTransform(getApplication().getCamera());
        getApplication().getViewPort().setBackgroundColor(ColorRGBA.Black);
        GuiGlobals.getInstance().setCursorEventsEnabled(true);
        unsubscribeSystems();
    }

    private void subscribeSystems(){
        gameEventBus.subscribe(ToggleSpectatorModeEvent.class, spectatorModeEventHandler);
        gameEventBus.subscribe(ToggleRotationDebugEvent.class, onRotationDebugEnable);
        visualEquipSystem.subscribe(gameEventBus);
        intentSystem.subscribe(gameEventBus);
    }

    private void unsubscribeSystems(){
        getApplication().getInputManager().removeListener(playerInputSystem);
        getApplication().getInputManager().removeListener(playerRotationControlSystem);
        gameEventBus.unsubscribe(ToggleSpectatorModeEvent.class, spectatorModeEventHandler);
        gameEventBus.unsubscribe(ToggleRotationDebugEvent.class, onRotationDebugEnable);
        visualEquipSystem.unsubscribe(gameEventBus);
        intentSystem.unsubscribe(gameEventBus);
    }


    public Node setupModelLight(Node node) {
        for (Spatial c : node.getChildren()) {

            if (c != null) {
                if (c instanceof Geometry g) {
                    Material originalMaterial = g.getMaterial();

                    if (originalMaterial.getTextureParam("BaseColorMap") != null) {
                        Material newMaterial = new Material(getApplication().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
                        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
                        g.setMaterial(newMaterial);
                    }
                } else if (c instanceof Node n) {
                    setupModelLight(n);
                }
            }
        }
        return node;
    }
}
