package com.mccurry.game.gameplay;

import com.epagagames.particles.Emitter;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.ArmatureMask;
import com.jme3.anim.SkinningControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.console.command.spectator.ToggleSpectatorModeEventHandler;
import com.mccurry.game.console.event.ToggleRotationDebugEvent;
import com.mccurry.game.console.event.ToggleSpectatorModeEvent;
import com.mccurry.game.console.model.GameContext;
import com.mccurry.game.constants.CameraConstants;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.system.EntitySystem;
import com.mccurry.game.gameplay.debug.DebugCommandListener;
import com.mccurry.game.gameplay.menu.pausemenu.event.GameplayChangePauseStateRequest;
import com.mccurry.game.gameplay.system.animation.component.ManualAnimationControlComponent;
import com.mccurry.game.gameplay.system.animation.control.ManualAnimationControl;
import com.mccurry.game.gameplay.system.attack.AttackSystem;
import com.mccurry.game.gameplay.system.attack.component.WeaponIdentityComponent;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.GunAnimationData;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.GunVfxData;
import com.mccurry.game.gameplay.system.attack.component.viewmodel.WeaponViewModelComponent;
import com.mccurry.game.gameplay.system.crosshair.CrosshairSystem;
import com.mccurry.game.gameplay.system.crosshair.component.CrosshairSourceComponent;
import com.mccurry.game.gameplay.system.crosshair.model.CrosshairType;
import com.mccurry.game.gameplay.system.equipment.core.EquipSystem;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.ViewmodelSystem;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.FirstPersonEquipSystem;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.HandsViewmodelComponent;
import com.mccurry.game.gameplay.system.equipment.visual.thirdperson.ThirdPersonVisualEquipSystem;
import com.mccurry.game.gameplay.system.equipment.core.component.EquipmentComponent;
import com.mccurry.game.gameplay.system.equipment.core.component.EquippableComponent;
import com.mccurry.game.gameplay.system.equipment.core.model.HandSlot;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.ViewmodelSourceComponent;
import com.mccurry.game.gameplay.system.equipment.visual.firstperson.component.FirstPersonViewTransformEffectsComponent;
import com.mccurry.game.gameplay.system.hitfeedback.model.HitFeedback;
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
import com.mccurry.game.gameplay.system.raycast.RaycastService;
import com.mccurry.game.gameplay.system.raycast.entityraycast.EntityRaycastSystem;
import com.mccurry.game.gameplay.system.raycast.entityraycast.component.EntityRaycastHitboxComponent;
import com.mccurry.game.gameplay.system.raycast.entityraycast.model.EntityRaycastHitbox;
import com.mccurry.game.gameplay.system.raycast.mapraycast.MapRaycastSystem;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.cursor.CursorService;
import com.mccurry.game.shared.sfx.factory.VfxFactories;
import com.mccurry.game.shared.sfx.model.Vfx;
import com.mccurry.game.shared.sfx.model.VfxType;
import com.mccurry.game.shared.sfx.player.VfxPlayer;
import com.mccurry.game.shared.sfx.pool.VfxPool;
import com.mccurry.game.util.event.EventHandlers;
import com.mccurry.map.model.BlockType;
import com.mccurry.map.model.Map;
import com.mccurry.map.visuals.BlockWorld;

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
    private CrosshairSystem crosshairSystem;
    private FirstPersonEquipSystem firstPersonEquipSystem;
    private ViewmodelSystem viewmodelSystem;

    private AttackSystem attackSystem;
    private EquipSystem equipSystem;

    private final EventBus gameEventBus = new EventBus();

    private final Node gameHudNode = new Node("Game HUD node");

    private ToggleSpectatorModeEventHandler spectatorModeEventHandler;
    private ViewPort fpViewport;

    private final Node universeNode = new Node();
    private final Node gameStateNode = new Node();

    private final SyncEventHandler<GameplayChangePauseStateRequest> onPauseRequest = EventHandlers.syncEventHandler(
            (event, elm) -> {
                switch (event.getDesiredNewState()) {
                    case PAUSED -> this.setEnabled(false);
                    case UNPAUSED -> this.setEnabled(true);
                }
            }
    );

    private DebugCommandListener debugCommandListener;

    private FilterPostProcessor firstPersonBloomPostProcessor;


    @Override
    protected void initialize(Application app) {
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
        for (int x = 1; x < mapX - 1; x++) {
            for (int z = 1; z < mapZ - 1; z++) {
                m.setBlockIdAtPosition(x, 1, z, BlockType.AIR.blockId);
            }
        }

        m.setBlockIdAtPosition(1, 0, 1, BlockType.STONE.blockId);
        m.setBlockIdAtPosition(0, 0, 1, BlockType.STONE.blockId);
        m.setBlockIdAtPosition(1, 0, 0, BlockType.STONE.blockId);
        m.setBlockIdAtPosition(11, 1, 11, BlockType.STONE.blockId);
        m.setBlockIdAtPosition(12, 1, 11, BlockType.STONE.blockId);

        var blockWorld = new BlockWorld(3, 16, mapX, mapY, mapZ, m, app.getAssetManager(), blockWorldNode);


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

        var fpCamera = camera.clone();
        fpCamera.setFrustumPerspective(45f, (float) fpCamera.getWidth() / fpCamera.getHeight(), 0.01f, 300f);

        // todo: cleanup this viewport
        fpViewport = renderManager.createMainView("View of firstPersonCamera", fpCamera);
        fpViewport.setClearFlags(false, true, false);

        var fpCameraNode = new CameraNode("First Person Camera Node", fpCamera);
        fpCameraNode.setCullHint(Spatial.CullHint.Never);

        var fpHands = setupModelLight((Node) assetManager.loadModel("Models/Hands_fp/Hands_fp.j3o"));
        fpHands.setLocalScale(0.4f);

        fpHands.move(-0.2f, -0.5f, 0.7f);
        fpHands.rotate(-FastMath.DEG_TO_RAD * 6, FastMath.DEG_TO_RAD * 4, 0);
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
        crosshairSystem = new CrosshairSystem(assetManager, gameHudNode);

        var raycastService = new RaycastService();
        var mapRaycastSystem = new MapRaycastSystem(blockWorld, raycastService);
        var entityRaycastSystem = new EntityRaycastSystem(entityStore, raycastService);

        attackSystem = new AttackSystem(mapRaycastSystem, entityRaycastSystem, gameEventBus, entityStore, vfxPlayer);
        equipSystem = new EquipSystem(gameEventBus, entityStore);

        thirdPersonVisualEquipSystem = new ThirdPersonVisualEquipSystem(entityStore);
        firstPersonEquipSystem = new FirstPersonEquipSystem(entityStore, gameEventBus);
        viewmodelSystem = new ViewmodelSystem(fpCameraNode, fpHandsNode, entityStore, ViewmodelSourceComponent.class);
        intentSystem = new IntentSystem(
                new IntentSystems(attackSystem, equipSystem),
                entityStore
        );

        universeNode.attachChild(blockWorldNode);

        gameStateNode.attachChild(universeNode);
        gameStateNode.attachChild(firstPersonRootNode);

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
        var firstPersonSource = new ViewmodelSourceComponent();
        var firstPersonEffects = new FirstPersonViewTransformEffectsComponent();
        var handsViewmodel = new HandsViewmodelComponent(fpHands);

        var player = entityStore.create(
                transformComponent,
                velocityComponent, // can move

                humanoidModelComponent, // required for visual equipping
                equipmentComponent,

                cameraHolder,  // camera is glued to this player
                cameraEffects, // can have camera effects
                firstPersonSource, // first person hands follow this entity
                firstPersonEffects, // can have first person view model effects
                handsViewmodel, // has first person hands
                playerControlled // player controlled
        );




        debugCommandListener = new DebugCommandListener(entityStore, assetManager);
        spectatorModeEventHandler = new ToggleSpectatorModeEventHandler(entityStore);


        // NIE ZMIENIAC KOLEJNOSCI
        entitySystems.add(new MovementEntitySystem(entityStore));
        entitySystems.add(viewmodelSystem); // ! must be after movement so theres no 1 frame delay between hands and position of holder

        // todo: clonowac camere + cleanup po wylaczeniu gry
        entitySystems.add(new CameraSystem(entityStore, app.getCamera()));
        // NIE ZMIENIAC KOLEJNOSCI

        setupState();
        spawnMob(universeNode);

        var weaponEntity1 = gunEntity(assetManager);
        gameEventBus.publish(new HandEquipIntentEvent(player, weaponEntity1, HandSlot.HAND_RIGHT), null);
    }

    private void spawnMob(Node universeNode) {
        var humanoidRootNode = setupModelLight((Node) getApplication().getAssetManager().loadModel("Models/HumanArmature/HumanArmature.j3o"));

        var transformComponent = new TransformComponent();
        var humanoidModelComponent = new HumanoidModelComponent(humanoidRootNode);
        transformComponent.getVisualNode().attachChild(humanoidRootNode);
        universeNode.attachChild(transformComponent.getOriginNode());
        transformComponent.getOriginNode().move(15, 3, 15);
        var velocityComponent = new VelocityComponent();
        var equipmentComponent = new EquipmentComponent();


        var matRed = new Material(getApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matRed.setColor("Color", ColorRGBA.Red);
        matRed.getAdditionalRenderState().setWireframe(true);

        var matGreen = matRed.clone();
        matGreen.setColor("Color", ColorRGBA.Green);

        var matBlue = matRed.clone();
        matBlue.setColor("Color", ColorRGBA.Blue);

        var armature = humanoidRootNode.getChild("Armature");
        var skinningControl = armature.getControl(SkinningControl.class);

        var raycastHitboxComponent = new EntityRaycastHitboxComponent(armature);

        var headHitbox = new Geometry(null, new Box(5 * 0.05f, 6 * 0.05f, 5.2f * 0.05f));
        headHitbox.move(0, 0.1f, 0.08f);
        var bodyHitbox = new Geometry(null, new Box(7 * 0.05f, 8 * 0.05f, 6f * 0.05f));
        bodyHitbox.move(0, 0.3f, 0.0f);

        var thighHitboxR = new Geometry(null, new Box(2 * 0.05f, 5 * 0.05f, 4f * 0.05f));
        thighHitboxR.move(0, 0.2f, 0.125f);
        var thighHitboxL = new Geometry(null, new Box(2 * 0.05f, 5 * 0.05f, 4f * 0.05f));
        thighHitboxL.move(0, 0.2f, 0.125f);

        var shinHitboxR = new Geometry(null, new Box(2 * 0.05f, 3 * 0.05f, 3f * 0.05f));
        shinHitboxR.move(0, 0.125f, 0.125f / 2);
        var shinHitboxL = new Geometry(null, new Box(2 * 0.05f, 3 * 0.05f, 3f * 0.05f));
        shinHitboxL.move(0, 0.125f, 0.125f / 2);

        var footHitboxR = new Geometry(null, new Box(2 * 0.05f, 4 * 0.05f, 2f * 0.05f));
        footHitboxR.move(0, 0.0f, 0);
        var footHitboxL = new Geometry(null, new Box(2 * 0.05f, 4 * 0.05f, 2f * 0.05f));
        footHitboxL.move(0, 0.0f, 0);

        var armHitboxR = new Geometry(null, new Box(2 * 0.05f, 6 * 0.05f, 2f * 0.05f));
        armHitboxR.move(0, 0.2f, 0);
        var armHitboxL = new Geometry(null, new Box(2 * 0.05f, 6 * 0.05f, 2f * 0.05f));
        armHitboxL.move(0, 0.2f, 0);

        var forearmHitboxR = new Geometry(null, new Box(2 * 0.05f, 4 * 0.05f, 2f * 0.05f));
        forearmHitboxR.move(0, 0.25f, 0);
        var forearmHitboxL = new Geometry(null, new Box(2 * 0.05f, 4 * 0.05f, 2f * 0.05f));
        forearmHitboxL.move(0, 0.25f, 0);

        var headRaycastHitbox = new EntityRaycastHitbox(headHitbox, headHitbox.getLocalTranslation(), new HitFeedback(7f));
        var bodyRaycastHitbox = new EntityRaycastHitbox(bodyHitbox, bodyHitbox.getLocalTranslation(), new HitFeedback(15f));
        var thighLRaycastHitbox = new EntityRaycastHitbox(thighHitboxL, thighHitboxL.getLocalTranslation(), new HitFeedback(8f));
        var thighRRaycastHitbox = new EntityRaycastHitbox(thighHitboxR, thighHitboxR.getLocalTranslation(), new HitFeedback(8f));
        var armHitboxRRaycastHitbox = new EntityRaycastHitbox(armHitboxR, armHitboxR.getLocalTranslation(), new HitFeedback(8f));
        var armHitboxLRaycastHitbox = new EntityRaycastHitbox(armHitboxL, armHitboxL.getLocalTranslation(), new HitFeedback(8f));

        raycastHitboxComponent.getHitboxes().addAll(
                List.of(
                        headRaycastHitbox,
                        bodyRaycastHitbox,

                        thighLRaycastHitbox,
                        thighRRaycastHitbox,

                        new EntityRaycastHitbox(shinHitboxR, shinHitboxR.getLocalTranslation(), null),
                        new EntityRaycastHitbox(shinHitboxL, shinHitboxL.getLocalTranslation(), null),

                        new EntityRaycastHitbox(footHitboxR, footHitboxR.getLocalTranslation(), null),
                        new EntityRaycastHitbox(footHitboxL, footHitboxL.getLocalTranslation(), null),

                        armHitboxRRaycastHitbox,
                        armHitboxLRaycastHitbox,

                        new EntityRaycastHitbox(forearmHitboxR, forearmHitboxR.getLocalTranslation(), null),
                        new EntityRaycastHitbox(forearmHitboxL, forearmHitboxL.getLocalTranslation(), null)
                )
        );

        raycastHitboxComponent.getHitboxes().forEach(hb -> {
            var mat = matBlue;
            if (hb.getGeometry() == headHitbox) {
                mat = matRed;
            }
            if(hb.getGeometry() == bodyHitbox){
                mat = matGreen;
            }
            hb.getGeometry().setMaterial(mat.clone());
        });

        var manualAnimControl = new ManualAnimationControl();
        var manualControlComponent = new ManualAnimationControlComponent(manualAnimControl);

        manualAnimControl.registerHitTarget(headRaycastHitbox, skinningControl.getArmature().getJoint("Head"));
        manualAnimControl.registerHitTarget(bodyRaycastHitbox, skinningControl.getArmature().getJoint("Spine"));

        manualAnimControl.registerHitTarget(armHitboxRRaycastHitbox, skinningControl.getArmature().getJoint("ArmR"));
        manualAnimControl.registerHitTarget(armHitboxLRaycastHitbox, skinningControl.getArmature().getJoint("ArmL"));

        manualAnimControl.registerHitTarget(thighRRaycastHitbox, skinningControl.getArmature().getJoint("ThighR"));
        manualAnimControl.registerHitTarget(thighLRaycastHitbox, skinningControl.getArmature().getJoint("ThighL"));

        armature.getControl(SkinningControl.class).getAttachmentsNode("Head").attachChild(headHitbox);
        armature.getControl(SkinningControl.class).getAttachmentsNode("Spine").attachChild(bodyHitbox);

        armature.getControl(SkinningControl.class).getAttachmentsNode("ThighR").attachChild(thighHitboxR);
        armature.getControl(SkinningControl.class).getAttachmentsNode("ThighL").attachChild(thighHitboxL);

        armature.getControl(SkinningControl.class).getAttachmentsNode("ShinR").attachChild(shinHitboxR);
        armature.getControl(SkinningControl.class).getAttachmentsNode("ShinL").attachChild(shinHitboxL);
//
        armature.getControl(SkinningControl.class).getAttachmentsNode("FootR").attachChild(footHitboxR);
        armature.getControl(SkinningControl.class).getAttachmentsNode("FootL").attachChild(footHitboxL);

        armature.getControl(SkinningControl.class).getAttachmentsNode("ArmR").attachChild(armHitboxR);
        armature.getControl(SkinningControl.class).getAttachmentsNode("ArmL").attachChild(armHitboxL);

        armature.getControl(SkinningControl.class).getAttachmentsNode("ForearmR").attachChild(forearmHitboxR);
        armature.getControl(SkinningControl.class).getAttachmentsNode("ForearmL").attachChild(forearmHitboxL);


        raycastHitboxComponent.getHitboxes().forEach(hb -> hb.getGeometry().setCullHint(Spatial.CullHint.Always));




        var created = entityStore.create(
                transformComponent,
                velocityComponent, // can move
                humanoidModelComponent, // required for visual equipping
                equipmentComponent,
                raycastHitboxComponent,
                manualControlComponent
        );

        raycastHitboxComponent.getHitboxes().forEach(hitbox -> {
            hitbox.getGeometry().setUserData("hitboxOwnerEntityId", created.getId());
        });


        var composer = armature.getControl(AnimComposer.class);

        var legsMask = new ArmatureMask();
        legsMask.addBones(skinningControl.getArmature(), "Pelvis", "ThighR", "ThighL", "ShinR", "ShinL", "FootR", "FootL");

        var upperBodyMask = new ArmatureMask();
        upperBodyMask.addBones(skinningControl.getArmature(),
                "Head",
                "Spine", "ArmL", "ArmR", "ForearmR", "ForearmL", "HandR", "HandL");

        composer.makeLayer("LowerBody", legsMask);
        composer.makeLayer("UpperBody", upperBodyMask);

        composer.setCurrentAction("Lower_Stand", "LowerBody");
        composer.setCurrentAction("Upper_AimRifle", "UpperBody");



        armature.addControl(manualAnimControl);


        // wazne! skinning control musi byc PO controlu ktory ustawia rotacje kosci
        armature.removeControl(skinningControl);
        armature.addControl(skinningControl);
        // wazne! skinning control musi byc PO controlu ktory ustawia rotacje kosci

        var weaponEntity1 = gunEntity(getApplication().getAssetManager());
        gameEventBus.publish(new HandEquipIntentEvent(created, weaponEntity1, HandSlot.HAND_RIGHT), null);
    }

    @Override
    public void update(float tpf) {
        if (!isEnabled()) {
            return;
        }

        long t1 = System.nanoTime();

        gameTimeInSeconds += tpf;

        debugCommandListener.setGameTimeInSeconds(gameTimeInSeconds);


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
        cleanupState();

        System.out.println("Game event bus:");
        gameEventBus._debug_getSubscribers().forEach(
                (clazz, sub) -> {
                    if (!sub.isEmpty()) {
                        System.out.println("still listening for clazz: %s".formatted(clazz));
                    }
                }
        );
        System.out.println("\norchestration bus:");
        getApplication().getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus()._debug_getSubscribers().forEach(
                (clazz, sub) -> {
                    if (!sub.isEmpty()) {
                        System.out.println("still listening for clazz: %s".formatted(clazz));
                    }
                }
        );
    }

    @Override
    protected void onEnable() {
        unpauseGame();
    }

    @Override
    protected void onDisable() {
        pauseGame();
    }

    private void setupState() {
        var consoleLauncher = getApplication().getStateManager().getState(GameStateOrchestrator.class).getCheatConsoleLauncher();
        if (consoleLauncher != null) {
            consoleLauncher.getCheatConsoleState().setGameContext(new GameContext(gameEventBus));
        }

        getApplication().getViewPort().setBackgroundColor(ColorRGBA.DarkGray);
        CursorService.cursorEnabled(false);
        setupSystems();

        var simpleApp = (SimpleApplication) getApplication();
        simpleApp.getRootNode().attachChild(gameStateNode);
        simpleApp.getGuiNode().attachChild(gameHudNode);

        var rootNode = ((SimpleApplication) getApplication()).getRootNode();
        getApplication().getRenderManager().getMainView("Default").detachScene(rootNode);
        getApplication().getRenderManager().getMainView("Default").attachScene(universeNode);


        firstPersonBloomPostProcessor = new FilterPostProcessor(getApplication().getAssetManager());
        // todo: to MUSI byc Objects bo inaczej cala scena sie bedzie swiecic w jasnych miejscach
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);

        bloom.setBloomIntensity(2f);
        bloom.setExposurePower(5f);
        bloom.setExposureCutOff(0.1f);

        // higher factor = less performance tax = looks shittier
        bloom.setDownSamplingFactor(2f);

        firstPersonBloomPostProcessor.addFilter(bloom);
        getApplication().getRenderManager().getMainView("Default").addProcessor(firstPersonBloomPostProcessor);
    }

    private void cleanupState() {
        var consoleLauncher = getApplication().getStateManager().getState(GameStateOrchestrator.class).getCheatConsoleLauncher();
        if (consoleLauncher != null) {
            consoleLauncher.getCheatConsoleState().setGameContext(null);
        }


        CameraConstants.applyDefaultTransform(getApplication().getCamera());
        getApplication().getViewPort().setBackgroundColor(ColorRGBA.Black);
        CursorService.cursorEnabled(true);
        cleanupSystems();
        getApplication().getRenderManager().removeMainView(fpViewport);


        var simpleApp = (SimpleApplication) getApplication();
        getApplication().getRenderManager().getMainView("Default").attachScene(simpleApp.getRootNode());
        getApplication().getRenderManager().getMainView("Default").detachScene(universeNode);

        gameStateNode.removeFromParent();
        gameHudNode.removeFromParent();

        getApplication().getRenderManager().getMainView("Default").removeProcessor(firstPersonBloomPostProcessor);
    }

    private void setupSystems() {
        var orchestrationBus = getApplication().getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus();
        setupControlsListener();
        gameEventBus.subscribe(ToggleSpectatorModeEvent.class, spectatorModeEventHandler);
        debugCommandListener.subscribe(gameEventBus);
        crosshairSystem.subscribe(gameEventBus);
        thirdPersonVisualEquipSystem.subscribe(gameEventBus);
        firstPersonEquipSystem.subscribe(gameEventBus);
        intentSystem.subscribe(gameEventBus);
        orchestrationBus.subscribe(GameplayChangePauseStateRequest.class, onPauseRequest);
    }

    private void cleanupSystems() {
        var orchestrationBus = getApplication().getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus();
        cleanupControlsListener();
        gameEventBus.unsubscribe(ToggleSpectatorModeEvent.class, spectatorModeEventHandler);
        debugCommandListener.unsubscribe(gameEventBus);
        crosshairSystem.unsubscribe(gameEventBus);
        thirdPersonVisualEquipSystem.unsubscribe(gameEventBus);
        firstPersonEquipSystem.unsubscribe(gameEventBus);
        intentSystem.unsubscribe(gameEventBus);
        orchestrationBus.unsubscribe(GameplayChangePauseStateRequest.class, onPauseRequest);
    }

    private void pauseGame() {
        cleanupControlsListener();

        setPaused(gameStateNode, true);
        setPaused(gameHudNode, true);
        CursorService.cursorEnabled(true);
    }

    private void unpauseGame() {
        setupControlsListener();

        setPaused(gameStateNode, false);
        setPaused(gameHudNode, false);
        CursorService.cursorEnabled(false);
    }

    public static void setPaused(Spatial spatial, boolean paused) {
        if (spatial instanceof Node node) {
            for (Spatial child : node.getChildren()) {
                setPaused(child, paused);
            }
        }

        for (int i = 0; i < spatial.getNumControls(); i++) {
            var c = spatial.getControl(i);
            if (c instanceof AbstractControl ac) {
                ac.setEnabled(!paused);
            }
        }

        if (spatial instanceof ParticleEmitter pe) {
            pe.setEnabled(!paused);
        } else if (spatial instanceof Emitter e) {
            e.setEnabled(!paused);
        }
    }


    private void setupControlsListener() {
        getApplication().getInputManager().addListener(playerInputSystem,
                MAPPING_KEY_W,
                MAPPING_KEY_S,
                MAPPING_KEY_A,
                MAPPING_KEY_D,
                MAPPING_MOUSE_LEFT,
                MAPPING_MOUSE_RIGHT
        );

        getApplication().getInputManager().addListener(playerRotationControlSystem,
                MAPPING_MOUSE_MOVE_X_LEFT,
                MAPPING_MOUSE_MOVE_X_RIGHT,
                MAPPING_MOUSE_MOVE_Y_DOWN,
                MAPPING_MOUSE_MOVE_Y_UP
        );
    }

    private void cleanupControlsListener() {
        getApplication().getInputManager().removeListener(playerInputSystem);
        getApplication().getInputManager().removeListener(playerRotationControlSystem);
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

    private Entity gunEntity(AssetManager assetManager){
//        var gun = setupModelLight(
//                (Node) assetManager.loadModel(
//                        "Models/double_barrel_fp/double_barrel_fp.j3o"
//                )
//        );
//
//        var weaponEntity1 = entityStore.create(
//                WeaponIdentityComponent.builder()
//                        .primaryActionIdentity(
//                                new WeaponIdentityComponent.WeaponActionIdentity(15f, 1f, 0)
//                        )
//                        .secondaryActionIdentity(
//                                new WeaponIdentityComponent.WeaponActionIdentity(15f, 1f, 0)
//                        )
//                        .build(),
//                CrosshairSourceComponent.builder()
//                        .crosshairType(CrosshairType.SHOTGUN)
//                        .build(),
//                EquippableComponent.builder()
//                        .handSlot(HandSlot.HAND_RIGHT)
//                        .handSlot(HandSlot.HAND_LEFT)
//                        .displayNode(setupModelLight((Node) assetManager.loadModel("Models/mp18_tp/mp18_tp.j3o")))
//                        .build(),
//                WeaponViewModelComponent.builder()
//                        .viewModel(gun)
//                        .primaryActionVfxData(
//                                GunVfxData.builder()
//                                        .muzzleNode(gun.getChild("Armature").getControl(SkinningControl.class).getAttachmentsNode("Muzzle_L"))
//                                        .muzzleFlash(VfxType.FP__MUZZLE_FLASH__DOUBLE_BARREL_1_L)
//                                        .muzzleFlashOffset(new Vector3f(0, 0, -0.15f))
//                                        .build()
//                        )
//                        .secondaryVfxData(
//                                GunVfxData.builder()
//                                        .muzzleNode(gun.getChild("Armature").getControl(SkinningControl.class).getAttachmentsNode("Muzzle_R"))
//                                        .muzzleFlash(VfxType.FP__MUZZLE_FLASH__DOUBLE_BARREL_1_R)
//                                        .muzzleFlashOffset(new Vector3f(0, 0, -0.15f))
//                                        .build()
//                        )
//                        .animComposer(gun.getChild("Armature").getControl(AnimComposer.class))
//                        .build()
//        );

        var gun = setupModelLight(
                (Node) assetManager.loadModel(
                        "Models/mp18_fp/mp18_fp.j3o"
                )
        );

        var weaponEntity1 = entityStore.create(
                WeaponIdentityComponent.builder()
                        .primaryActionIdentity(new WeaponIdentityComponent.WeaponActionIdentity(3f, 1f / 8, 0)).build(),
                CrosshairSourceComponent.builder()
                        .crosshairType(CrosshairType.SMG)
                        .build(),
                EquippableComponent.builder()
                        .handSlot(HandSlot.HAND_RIGHT)
                        .handSlot(HandSlot.HAND_LEFT)
                        .displayNode(setupModelLight((Node) assetManager.loadModel("Models/mp18_tp/mp18_tp.j3o")))
                        .build(),
                WeaponViewModelComponent.builder()
                        .viewModel(gun)
                        .primaryActionVfxData(
                                GunVfxData.builder()
                                        .muzzleNode(gun.getChild("Armature").getControl(SkinningControl.class).getAttachmentsNode("Muzzle"))
                                        .bulletCaseEjectionNode(gun.getChild("Armature").getControl(SkinningControl.class).getAttachmentsNode("CasingSpawnpoint"))
                                        .muzzleFlash(VfxType.FP__MUZZLE_FLASH__SMG_1)
                                        .muzzleFlashOffset(new Vector3f(0, 0, -0.15f))
                                        .caseEjection(VfxType.FP__BULLET_CASE__SMG_1)
                                        .build()
                        )
                        .animComposer(gun.getChild("Armature").getControl(AnimComposer.class))
                        .gunAnimationData(
                                GunAnimationData.builder()
                                        .lockIdleAnimName("Lock_Idle")
                                        .lockRecoilAnimName("Lock_Pull")
                                        .lockReturnAnimName("Lock_Return")
                                        .build()
                        )
                        .build()
        );
        return weaponEntity1;
    }

}
