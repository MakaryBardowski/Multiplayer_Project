package com.mccurry.game.gameplay.debug;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.console.event.ToggleHitboxDebugEvent;
import com.mccurry.game.console.event.ToggleMapDebugEvent;
import com.mccurry.game.console.event.ToggleRotationDebugEvent;
import com.mccurry.game.gameplay.core.ecs.entity.Entity;
import com.mccurry.game.gameplay.core.ecs.entity.EntityCallback;
import com.mccurry.game.gameplay.core.ecs.entity.EntityStore;
import com.mccurry.game.gameplay.system.modelhierarchy.component.HumanoidModelComponent;
import com.mccurry.game.gameplay.system.raycast.entityraycast.component.EntityRaycastHitboxComponent;
import com.mccurry.game.gameplay.system.transform.TransformComponent;
import com.mccurry.game.util.debug.GeometryUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import static com.mccurry.game.gameplay.core.ecs.CoreConstants.BLOCK_SIZE;
import static com.mccurry.game.gameplay.core.ecs.CoreConstants.CHUNK_SIZE;

public class DebugCommandListener implements EventSubscriber {
    private static final String USER_DATA_DEBUG_ROTATION = "__DEBUG_ROTATION_NODE";

    private final AssetManager assetManager;

    @Setter
    private double gameTimeInSeconds;

    private final SyncEventHandler<ToggleRotationDebugEvent> onRotationDebugToggle;
    private final SyncEventHandler<ToggleHitboxDebugEvent> onHitboxDebugToggle;
    private final SyncEventHandler<ToggleMapDebugEvent> onMapDebugToggle;

    @Getter
    private final Node debugNode = new Node("Debug Node");

    public DebugCommandListener(EntityStore entityStore, AssetManager assetManager) {
        this.assetManager = assetManager;
        onRotationDebugToggle = new SyncEventHandler<ToggleRotationDebugEvent>() {
            private boolean enabledMain = false;
            private boolean enabledGranular = false;

            @Override
            public void handle(@NonNull ToggleRotationDebugEvent event, @Nullable EventLifecycleManager<ToggleRotationDebugEvent> lifecycleManager) {
                if (!event.isGranular()) {
                    enabledMain = !enabledMain;
                } else {
                    enabledGranular = !enabledGranular;
                }

                entityStore.forEachEntityWithComponents(0, gameTimeInSeconds, new EntityCallback() {
                    @Override
                    public void execute(Entity entityWithRequiredComponents, float tpf, double currentGameTimeInSeconds) {
                        var transform = entityStore.getComponent(entityWithRequiredComponents, TransformComponent.class);


                        toggleForwardAxis(enabledMain, transform.getOriginNode(), 2, ColorRGBA.Blue);
                        toggleForwardAxis(enabledMain, transform.getVisualNode(), 4, ColorRGBA.Magenta);


                        var humanModelComponent = entityStore.getComponent(entityWithRequiredComponents, HumanoidModelComponent.class);
                        if (humanModelComponent != null) {
                            var rightHandNode = humanModelComponent.getRightHandAttachmentNode();
                            toggleAxes(enabledGranular, rightHandNode);

                            var leftHandNode = humanModelComponent.getLeftHandAttachmentNode();
                            toggleAxes(enabledGranular, leftHandNode);

                            var headNode = humanModelComponent.getHeadAttachmentNode();
                            toggleAxes(enabledGranular, headNode);
                        }
                    }
                }, TransformComponent.class);
            }
        };


        onHitboxDebugToggle = new SyncEventHandler<ToggleHitboxDebugEvent>() {
            @Override
            public void handle(@NonNull ToggleHitboxDebugEvent event, @Nullable EventLifecycleManager<ToggleHitboxDebugEvent> lifecycleManager) {
                entityStore.forEachEntityWithComponents(
                        0, gameTimeInSeconds, new EntityCallback() {
                            @Override
                            public void execute(Entity entityWithRequiredComponents, float tpf, double currentGameTimeInSeconds) {

                                var hitboxComponent = entityStore.getComponent(entityWithRequiredComponents, EntityRaycastHitboxComponent.class);

                                hitboxComponent.getHitboxes().forEach(
                                        hitbox -> {
                                            var geom = hitbox.getGeometry();

                                            if (geom.getCullHint().equals(Spatial.CullHint.Always)) {
                                                geom.setCullHint(Spatial.CullHint.Never);
                                            } else {
                                                geom.setCullHint(Spatial.CullHint.Always);
                                            }

                                        }
                                );

                            }
                        }, EntityRaycastHitboxComponent.class
                );
            }
        };

        onMapDebugToggle = new SyncEventHandler<ToggleMapDebugEvent>() {
            private boolean enabledChunk = false;
            private Node chunkGrid = null;

            @Override
            public void handle(@NonNull ToggleMapDebugEvent event, @Nullable EventLifecycleManager<ToggleMapDebugEvent> lifecycleManager) {
                switch (event.getMapDebugType()) {
                    case CHUNK -> {
                        enabledChunk = !enabledChunk;
                    }
                }

                if (enabledChunk) {
                    chunkGrid = createGrid(assetManager, CHUNK_SIZE);
                    debugNode.attachChild(chunkGrid);
                } else {
                    chunkGrid.removeFromParent();
                }

            }
        };

    }


    @Override
    public void subscribe(@NonNull EventBus eventBus) {
        eventBus.subscribe(ToggleRotationDebugEvent.class, onRotationDebugToggle);
        eventBus.subscribe(ToggleHitboxDebugEvent.class, onHitboxDebugToggle);
        eventBus.subscribe(ToggleMapDebugEvent.class, onMapDebugToggle);
    }

    @Override
    public void unsubscribe(@NonNull EventBus eventBus) {
        eventBus.unsubscribe(ToggleRotationDebugEvent.class, onRotationDebugToggle);
        eventBus.unsubscribe(ToggleHitboxDebugEvent.class, onHitboxDebugToggle);
        eventBus.unsubscribe(ToggleMapDebugEvent.class, onMapDebugToggle);
    }

    private void toggleAxes(boolean enabled, Node parentNode) {
        if (enabled) {
            var rightHandRotDebugNode = ((Node) parentNode.getUserData(USER_DATA_DEBUG_ROTATION));
            ;

            if (rightHandRotDebugNode == null) {
                rightHandRotDebugNode = new Node();

                var axisX = GeometryUtil.createArrow(0.5f, Vector3f.UNIT_X, ColorRGBA.Red, assetManager);
                var axisY = GeometryUtil.createArrow(0.5f, Vector3f.UNIT_Y, ColorRGBA.Green, assetManager);
                var axisZ = GeometryUtil.createArrow(0.5f, Vector3f.UNIT_Z, ColorRGBA.Blue, assetManager);
                rightHandRotDebugNode.attachChild(axisX);
                rightHandRotDebugNode.attachChild(axisY);
                rightHandRotDebugNode.attachChild(axisZ);
            }

            parentNode.attachChild(
                    rightHandRotDebugNode
            );
            parentNode.setUserData(USER_DATA_DEBUG_ROTATION, rightHandRotDebugNode);
        } else {
            var node = ((Node) parentNode.getUserData(USER_DATA_DEBUG_ROTATION));
            if (node != null) {
                node.removeFromParent();
            }
            parentNode.setUserData(USER_DATA_DEBUG_ROTATION, null);
        }
    }

    private void toggleForwardAxis(boolean enabled, Node parentNode, float arrowLength, ColorRGBA color) {

        if (enabled == true) {
            var existingDebug = ((Geometry) parentNode.getUserData(USER_DATA_DEBUG_ROTATION));


            if (existingDebug == null) {
                existingDebug = GeometryUtil.createArrow(arrowLength, Vector3f.UNIT_Z, color, assetManager);
            }

            parentNode.attachChild(
                    existingDebug
            );

            parentNode.setUserData(USER_DATA_DEBUG_ROTATION, existingDebug);
        } else {
            var geom = ((Geometry) parentNode.getUserData(USER_DATA_DEBUG_ROTATION));
            if (geom != null) {
                geom.removeFromParent();
            }
            parentNode.setUserData(USER_DATA_DEBUG_ROTATION, null);
        }
    }

    public static Node createGrid(AssetManager assetManager, int gridCellSize) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setLineWidth(2f);

        mat.setColor("Color", ColorRGBA.Magenta);
        Node grid = new Node("Debug Grid");
        //wizualizacja glownej siatki
        for (int i = -15; i < 15; i++) {
            for (int j = -15; j < 15; j++) {
                Geometry g = new Geometry("wireframe grid", new Grid(2, 2, 1f));

                g.setMaterial(mat);
                g.center().move(gridCellSize * i + 0.5f, BLOCK_SIZE + .01f, gridCellSize * j + 0.5f);
                g.scale(gridCellSize);
                grid.attachChild(g);
            }
        }
        return grid;
    }
}
