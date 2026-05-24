package com.mccurry.game.gameplay.core;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
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
import com.mccurry.game.gameplay.system.attack.component.WeaponIdentityComponent;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.GunAnimationData;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.GunVfxData;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.GunViewModelComponent;
import com.mccurry.game.gameplay.system.equipment.core.EquipSystem;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.FirstPersonViewSystem;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.FirstPersonEquipSystem;
import com.mccurry.game.gameplay.system.equipment.visual.ThirdPersonVisualEquipSystem;
import com.mccurry.game.gameplay.system.equipment.core.component.EquipmentComponent;
import com.mccurry.game.gameplay.system.equipment.core.component.EquippableComponent;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.FirstPersonViewSourceComponent;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.FirstPersonViewTransformEffectsComponent;
import com.mccurry.game.gameplay.system.intent.event.HandEquipIntentEvent;
import com.mccurry.game.gameplay.system.intent.resolver.model.IntentSystems;
import com.mccurry.game.gameplay.system.intent.system.IntentSystem;
import com.mccurry.game.gameplay.system.modelhierarchy.component.HumanoidModelComponent;
import com.mccurry.game.gameplay.system.movement.MovementEntitySystem;
import com.mccurry.game.gameplay.system.movement.component.VelocityComponent;
import com.mccurry.game.gameplay.system.player.CameraSystem;
import com.mccurry.game.gameplay.system.player.PlayerInputSystem;
import com.mccurry.game.gameplay.system.player.PlayerRotationControlSystem;
import com.mccurry.game.gameplay.system.player.component.camera.CameraTransformEffectsComponent;
import com.mccurry.game.gameplay.system.player.component.camera.CameraHolderComponent;
import com.mccurry.game.gameplay.system.player.component.control.PlayerControlledComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.keyBinding.KeyMappingRegistry;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.sfx.factory.VfxFactories;
import com.mccurry.game.shared.sfx.model.Vfx;
import com.mccurry.game.shared.sfx.model.VfxType;
import com.mccurry.game.shared.sfx.player.VfxPlayer;
import com.mccurry.game.shared.sfx.pool.VfxPool;
import com.mccurry.game.util.debug.GeometryUtil;
import com.mccurry.game.util.event.EventHandlers;
import com.mccurry.map.model.BlockType;
import com.mccurry.map.model.Map;
import com.mccurry.map.visuals.BlockWorld;
import com.simsilica.lemur.GuiGlobals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mccurry.game.keyBinding.KeyMappingRegistry.*;

public class GameState extends BaseAppState {
    private double gameTimeInSeconds = 0;
    private EntityStore entityStore = new EntityStore();

    private final List<EntitySystem> entitySystems = new ArrayList<>();

    private PlayerInputSystem playerInputSystem;
    private PlayerRotationControlSystem playerRotationControlSystem;
    private IntentSystem intentSystem;
    private ThirdPersonVisualEquipSystem thirdPersonVisualEquipSystem;
    private FirstPersonEquipSystem firstPersonEquipSystem;
    private FirstPersonViewSystem firstPersonViewSystem;

    private AttackSystem attackSystem;
    private EquipSystem equipSystem;

    private final EventBus gameEventBus = new EventBus();

    private ToggleSpectatorModeEventHandler spectatorModeEventHandler;
    private ViewPort fpViewport;
    private SyncEventHandler<ToggleRotationDebugEvent> onRotationDebugEnable = EventHandlers.syncEventHandler(
            (event, elm) -> {
                entityStore.forEachEntityWithComponents(0, gameTimeInSeconds, new EntityCallback() {
                    @Override
                    public void execute(Entity entityWithRequiredComponents, float tpf, double currentGameTimeInSeconds) {

                        var transform = entityStore.getComponent(entityWithRequiredComponents, TransformComponent.class);
                        transform.getOriginNode().attachChild(
                                GeometryUtil.createArrow(2, Vector3f.UNIT_Z, ColorRGBA.Blue, getApplication().getAssetManager())
                        );

                        transform.getVisualNode().attachChild(
                                GeometryUtil.createArrow(4, Vector3f.UNIT_Z, ColorRGBA.Magenta, getApplication().getAssetManager())
                        );

                        var humanModelComponent = entityStore.getComponent(entityWithRequiredComponents, HumanoidModelComponent.class);
                        if (humanModelComponent != null && event.isGranular()) {
                            humanModelComponent.getRightHandAttachmentNode().attachChild(
                                    GeometryUtil.createArrow(0.5f, Vector3f.UNIT_Z, ColorRGBA.Blue, getApplication().getAssetManager())
                            );
                            humanModelComponent.getRightHandAttachmentNode().attachChild(
                                    GeometryUtil.createArrow(0.5f, Vector3f.UNIT_X, ColorRGBA.Red, getApplication().getAssetManager())
                            );
                            humanModelComponent.getRightHandAttachmentNode().attachChild(
                                    GeometryUtil.createArrow(0.5f, Vector3f.UNIT_Y, ColorRGBA.Green, getApplication().getAssetManager())
                            );


                            humanModelComponent.getLeftHandAttachmentNode().attachChild(
                                    GeometryUtil.createArrow(0.5f, Vector3f.UNIT_Z, ColorRGBA.Blue, getApplication().getAssetManager())
                            );
                            humanModelComponent.getLeftHandAttachmentNode().attachChild(
                                    GeometryUtil.createArrow(0.5f, Vector3f.UNIT_X, ColorRGBA.Red, getApplication().getAssetManager())
                            );
                            humanModelComponent.getLeftHandAttachmentNode().attachChild(
                                    GeometryUtil.createArrow(0.5f, Vector3f.UNIT_Y, ColorRGBA.Green, getApplication().getAssetManager())
                            );


                            humanModelComponent.getHeadAttachmentNode().attachChild(
                                    GeometryUtil.createArrow(0.5f, Vector3f.UNIT_Z, ColorRGBA.Blue, getApplication().getAssetManager())
                            );
                            humanModelComponent.getHeadAttachmentNode().attachChild(
                                    GeometryUtil.createArrow(0.5f, Vector3f.UNIT_X, ColorRGBA.Red, getApplication().getAssetManager())
                            );
                            humanModelComponent.getHeadAttachmentNode().attachChild(
                                    GeometryUtil.createArrow(0.5f, Vector3f.UNIT_Y, ColorRGBA.Green, getApplication().getAssetManager())
                            );
                        }
                    }
                }, TransformComponent.class);
            }
    );


    @Override
    protected void initialize(Application app) {
        var assetManager = getApplication().getAssetManager();
        var renderManager = getApplication().getRenderManager();
        var camera = getApplication().getCamera();


        var vfxPool = new VfxPool();
        Consumer<Vfx> onFinishedPlaying = (vfx) -> {
            vfxPool.reclaim(vfx);
        };
        var vfxFactories = VfxFactories.getFactories(assetManager, onFinishedPlaying);
        vfxPool.initialize(vfxFactories);
        var vfxPlayer = new VfxPlayer(vfxPool);


        var firstPersonRootNode = new Node();
        var universeNode = new Node();


        var fpCamera = camera.clone();
        fpCamera.setFrustumPerspective(45f, (float) fpCamera.getWidth() / fpCamera.getHeight(), 0.01f, 300f);

        // todo: cleanup this viewport
        fpViewport = renderManager.createMainView("View of firstPersonCamera", fpCamera);
        fpViewport.setClearFlags(false, true, true);

        var fpCameraNode = new CameraNode("First Person Camera Node", fpCamera);
        fpCameraNode.setCullHint(Spatial.CullHint.Never);

        var fpHands = setupModelLight((Node) assetManager.loadModel("Models/Hands_fp/Hands_fp.j3o"));
        fpHands.setLocalScale(0.4f);

        fpHands.move(-0.2f,-0.5f,0.7f);
        fpHands.rotate(-FastMath.DEG_TO_RAD*6, FastMath.DEG_TO_RAD*4,0);
        fpHands.getChild("Armature").getControl(AnimComposer.class).setCurrentAction("Hold_SMG_1");

        var fpHandsNode = new Node("FP view node");
        fpHandsNode.attachChild(fpHands);
        fpCameraNode.attachChild(fpHandsNode);

        // if a node that we want to add to a viewport is not attached to root node, it crashes :) gotta love jme
        firstPersonRootNode.attachChild(fpCameraNode);
        fpViewport.attachScene(fpCameraNode);

//        fpCameraNode.attachChild(createViewingFrustum(fpCameraNode.getCamera(), assetManager));




        playerInputSystem = new PlayerInputSystem(entityStore, gameEventBus);
        playerRotationControlSystem = new PlayerRotationControlSystem(entityStore);

        attackSystem = new AttackSystem(entityStore,vfxPlayer);
        equipSystem = new EquipSystem(gameEventBus, entityStore);

        thirdPersonVisualEquipSystem = new ThirdPersonVisualEquipSystem(entityStore);
        firstPersonEquipSystem = new FirstPersonEquipSystem(entityStore);
        firstPersonViewSystem = new FirstPersonViewSystem(fpCameraNode, fpHandsNode, entityStore, FirstPersonViewSourceComponent.class);
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
        for (int x = 0; x < mapX; x++) {
            for (int z = 0; z < mapZ; z++) {
                m.setBlockIdAtPosition(x, 1, z, BlockType.OFFICE_FLOOR.blockId);
            }
        }
        for (int x = 1; x < mapX-1; x++) {
            for (int z = 1; z < mapZ-1; z++) {
                m.setBlockIdAtPosition(x, 1, z, BlockType.AIR.blockId);
            }
        }

        m.setBlockIdAtPosition(1, 0, 1, BlockType.STONE.blockId);
        m.setBlockIdAtPosition(0, 0, 1, BlockType.STONE.blockId);
        m.setBlockIdAtPosition(1, 0, 0, BlockType.STONE.blockId);
        m.setBlockIdAtPosition(11, 1, 11, BlockType.STONE.blockId);
        m.setBlockIdAtPosition(12, 1, 11, BlockType.STONE.blockId);

        var b = new BlockWorld(3, 16, mapX, mapY, mapZ, m, app.getAssetManager(), blockWorldNode);


        universeNode.attachChild(blockWorldNode);
        var rootNode = ((SimpleApplication)getApplication()).getRootNode();
        rootNode.attachChild(universeNode);
        rootNode.attachChild(firstPersonRootNode);

        // todo: to rozpierdala stan gry po wyjsciu, trzeba w onDisable() przywracac rootNode
        getApplication().getRenderManager().getMainView("Default").detachScene(rootNode);
        getApplication().getRenderManager().getMainView("Default").attachScene(universeNode);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.5f));
        universeNode.addLight(al);
        firstPersonRootNode.addLight(al);


        var humanoidRootNode = setupModelLight((Node) getApplication().getAssetManager().loadModel("Models/HumanArmature/HumanArmature.j3o"));

        var transformComponent = new TransformComponent();
        var humanoidModelComponent = new HumanoidModelComponent(humanoidRootNode);
        transformComponent.getVisualNode().attachChild(humanoidRootNode);
        universeNode.attachChild(transformComponent.getOriginNode());
        transformComponent.getOriginNode().move(0, 3, 0);
        var velocityComponent = new VelocityComponent();
        var playerControlled = new PlayerControlledComponent();
        var cameraHolder = new CameraHolderComponent(new Vector3f(0, 2.067f, 0));
        var cameraEffects = new CameraTransformEffectsComponent();
        var equipmentComponent = new EquipmentComponent();
        var firstPersonSource = new FirstPersonViewSourceComponent();
        var firstPersonEffects = new FirstPersonViewTransformEffectsComponent();

        var player = entityStore.create(
                transformComponent,
                velocityComponent, // can move

                humanoidModelComponent, // required for visual equipping
                equipmentComponent,

                cameraHolder,  // camera is glued to this player
                cameraEffects, // can have camera effects
                firstPersonSource, // first person hands follow this entity
                firstPersonEffects, // can have first person view model effects

                playerControlled // player controlled
        );


        spectatorModeEventHandler = new ToggleSpectatorModeEventHandler(entityStore);


        entitySystems.add(new MovementEntitySystem(entityStore));
        entitySystems.add(firstPersonViewSystem); // ! must be after movement so theres no 1 frame delay between hands and position of holder

        // todo: clonowac camere + cleanup po wylaczeniu gry
        entitySystems.add(new CameraSystem(entityStore, app.getCamera()));

        subscribeSystems();


        var gun =   setupModelLight(
                (Node) assetManager.loadModel("Models/mp18_fp/mp18_fp.j3o")
        );


        fpHands.getChild(0).getControl(SkinningControl.class).getAttachmentsNode("HandAttachmentBoneR").attachChild(
                gun
        );


        System.out.println("Gun children "+gun.getChildren().stream().map(s -> s.getName()).toList());

        var weaponEntity1 = entityStore.create(
                new WeaponIdentityComponent(10f, 1f/8, 0),
                EquippableComponent.builder()
                        .handSlot(HandSlot.HAND_RIGHT)
                        .handSlot(HandSlot.HAND_LEFT)
                        .displayNode(setupModelLight((Node) assetManager.loadModel("Models/mp18_tp/mp18_tp.j3o")))
                        .build(),
                GunViewModelComponent.builder()
                        .viewModel(gun)
                        .muzzleNode(gun.getChild("Armature").getControl(SkinningControl.class).getAttachmentsNode("Muzzle"))
                        .bulletCaseEjectionNode(gun.getChild("Armature").getControl(SkinningControl.class).getAttachmentsNode("CasingSpawnpoint"))
                        .animComposer(gun.getChild("Armature").getControl(AnimComposer.class))
                        .gunAnimationData(
                                GunAnimationData.builder()
                                        .lockIdleAnimName("Lock_Idle")
                                        .lockRecoilAnimName("Lock_Pull")
                                        .lockReturnAnimName("Lock_Return")
                                        .build()
                        )
                        .gunVfxData(
                                GunVfxData.builder()
                                        .muzzleFlash(VfxType.FP__MUZZLE_FLASH__SMG_1)
                                        .caseEjection(VfxType.FP__BULLET_CASE__SMG_1)
                                        .muzzleFlashOffset(new Vector3f(0,0,-0.15f))
                                        .build()
                        )
                        .build()
        );


        gameEventBus.publish(new HandEquipIntentEvent(player, weaponEntity1, HandSlot.HAND_RIGHT), null);


        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        // todo: to MUSI byc Objects bo inaczej cala scena sie bedzie swiecic w jasnych miejscach
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);

        bloom.setBloomIntensity(2f);
        bloom.setExposurePower(5f);
        bloom.setExposureCutOff(0.1f);

        // higher factor = less performance tax = looks shittier
        bloom.setDownSamplingFactor(1f);

        fpp.addFilter(bloom);
        renderManager.getMainView("Default").addProcessor(fpp);
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
        if (consoleLauncher != null) {
            consoleLauncher.getCheatConsoleState().setGameContext(new GameContext(gameEventBus));
        }

        getApplication().getViewPort().setBackgroundColor(ColorRGBA.Cyan);
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
        subscribeSystems();
    }

    @Override
    protected void onDisable() {
        var consoleLauncher = getApplication().getStateManager().getState(GameStateOrchestrator.class).getCheatConsoleLauncher();
        if (consoleLauncher != null) {
            consoleLauncher.getCheatConsoleState().setGameContext(null);
        }


        CameraConstants.applyDefaultTransform(getApplication().getCamera());
        getApplication().getViewPort().setBackgroundColor(ColorRGBA.Black);
        GuiGlobals.getInstance().setCursorEventsEnabled(true);
        unsubscribeSystems();
        getApplication().getRenderManager().removeMainView(fpViewport);
    }

    private void subscribeSystems() {
        gameEventBus.subscribe(ToggleSpectatorModeEvent.class, spectatorModeEventHandler);
        gameEventBus.subscribe(ToggleRotationDebugEvent.class, onRotationDebugEnable);
        thirdPersonVisualEquipSystem.subscribe(gameEventBus);
        firstPersonEquipSystem.subscribe(gameEventBus);
        intentSystem.subscribe(gameEventBus);
    }

    private void unsubscribeSystems() {
        getApplication().getInputManager().removeListener(playerInputSystem);
        getApplication().getInputManager().removeListener(playerRotationControlSystem);
        gameEventBus.unsubscribe(ToggleSpectatorModeEvent.class, spectatorModeEventHandler);
        gameEventBus.unsubscribe(ToggleRotationDebugEvent.class, onRotationDebugEnable);
        thirdPersonVisualEquipSystem.unsubscribe(gameEventBus);
        firstPersonEquipSystem.unsubscribe(gameEventBus);
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
