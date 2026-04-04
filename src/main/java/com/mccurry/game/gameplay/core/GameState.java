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
import com.mccurry.game.constants.CameraConstants;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityCallback;
import com.mccurry.game.gameplay.system.attack.component.WeaponComponent;
import com.mccurry.game.gameplay.system.equipment.EquipmentComponent;
import com.mccurry.game.gameplay.system.equipment.model.HandSlot;
import com.mccurry.game.gameplay.system.intent.system.IntentSystem;
import com.mccurry.game.gameplay.system.movement.MovementSystem;
import com.mccurry.game.gameplay.system.movement.component.VelocityComponent;
import com.mccurry.game.gameplay.system.player.CameraSystem;
import com.mccurry.game.gameplay.system.player.PlayerInputSystem;
import com.mccurry.game.gameplay.system.player.PlayerRotationControlSystem;
import com.mccurry.game.gameplay.system.player.component.camera.CameraEffectsComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.player.component.control.PlayerControlledComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.keyBinding.KeyMappingRegistry;
import com.mccurry.game.util.debug.GeometryUtil;
import com.mccurry.game.util.event.EventHandlers;
import com.mccurry.map.model.BlockType;
import com.mccurry.map.model.Map;
import com.mccurry.map.visuals.BlockWorld;
import com.simsilica.lemur.GuiGlobals;

import java.util.ArrayList;
import java.util.List;

import static com.mccurry.game.keyBinding.KeyMappingRegistry.*;

public class GameState extends BaseAppState {
    private EntityStore entityStore = new EntityStore();

    private final List<com.mccurry.game.gameplay.core.ecs.system.System> regularSystems = new ArrayList<>();

    private PlayerInputSystem playerInputSystem;
    private PlayerRotationControlSystem playerRotationControlSystem;
    private IntentSystem intentSystem;

    private final EventBus gameEventBus = new EventBus();

    private ToggleSpectatorModeEventHandler spectatorModeEventHandler;
    private SyncEventHandler<ToggleRotationDebugEvent> onRotationDebugEnable = EventHandlers.syncEventHandler(
            (event, elm) -> {
                entityStore.forEachEntityWithComponents(0, new EntityCallback() {
                    @Override
                    public void execute(Entity entityWithRequiredComponents, float tpf) {

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
        regularSystems.add(new MovementSystem(entityStore));
        regularSystems.add(new CameraSystem(entityStore,app.getCamera()));



        playerInputSystem = new PlayerInputSystem(entityStore,gameEventBus);
        playerRotationControlSystem = new PlayerRotationControlSystem(entityStore);
        intentSystem = new IntentSystem(gameEventBus, entityStore);


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


        for (int i = 0; i < 1; i++) {
            var node = (Node) getApplication().getAssetManager().loadModel("Models/HumanArmature/HumanArmature.j3o");
            setupModelLight(node);


            var transformComponent = new TransformComponent();
            transformComponent.getVisualNode().attachChild(node);
            ((SimpleApplication) getApplication()).getRootNode().attachChild(transformComponent.getOriginNode());
            transformComponent.getOriginNode().move(0,3,0);


            var velocityComponent = new VelocityComponent();
            entityStore.create(transformComponent, velocityComponent);
        }

        var node = (Node) getApplication().getAssetManager().loadModel("Models/HumanArmature/HumanArmature.j3o");
        setupModelLight(node);



        var weaponEntity = entityStore.create(
                new WeaponComponent(10f,0.1f)
        );



        var transformComponent = new TransformComponent();
        transformComponent.getVisualNode().attachChild(node);
        ((SimpleApplication) getApplication()).getRootNode().attachChild(transformComponent.getOriginNode());
        transformComponent.getOriginNode().move(0,3,0);
        var velocityComponent = new VelocityComponent();
        var playerControlled = new PlayerControlledComponent();
        var cameraHolder = new CameraHolderComponent(new Vector3f(0,2.067f,0));
        var cameraEffects = new CameraEffectsComponent();
        var equipmentComponent = new EquipmentComponent();


        equipmentComponent.putTool(HandSlot.HAND_RIGHT,weaponEntity);

        entityStore.create(
                transformComponent,
                velocityComponent, // can move
                playerControlled, // player controlled
                cameraHolder,  // camera is glued to this player
                cameraEffects, // can have camera effects
                equipmentComponent
        );



        spectatorModeEventHandler = new ToggleSpectatorModeEventHandler(entityStore);
    }

    @Override
    public void update(float tpf) {
        long t1 = System.nanoTime();

        for (var system : regularSystems) {
            system.processEligibleEntities(tpf);
        }

        long t2 = System.nanoTime();

        long res = t2 - t1;

        double s = (double) res / 1000000000;

//        System.out.println(s+"s");

    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        getApplication().getViewPort().setBackgroundColor(ColorRGBA.Cyan);
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
        gameEventBus.subscribe(ToggleSpectatorModeEvent.class, spectatorModeEventHandler);
        gameEventBus.subscribe(ToggleRotationDebugEvent.class, onRotationDebugEnable);
        intentSystem.subscribe(gameEventBus);
    }

    @Override
    protected void onDisable() {
        CameraConstants.applyDefaultTransform(getApplication().getCamera());
        getApplication().getViewPort().setBackgroundColor(ColorRGBA.Black);
        getApplication().getInputManager().removeListener(playerInputSystem);
        getApplication().getInputManager().removeListener(playerRotationControlSystem);
        GuiGlobals.getInstance().setCursorEventsEnabled(true);
        gameEventBus.unsubscribe(ToggleSpectatorModeEvent.class, spectatorModeEventHandler);
        gameEventBus.unsubscribe(ToggleRotationDebugEvent.class, onRotationDebugEnable);
        intentSystem.unsubscribe(gameEventBus);
    }


    public void setupModelLight(Node node) {
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
    }
}
