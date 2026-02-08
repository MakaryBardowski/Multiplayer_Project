package com.mccurry.game.boot.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.boot.events.AssetLoadingPercentAdvanceEvent;
import com.mccurry.game.boot.events.BootUiStateSetupCompleteEvent;
import com.mccurry.game.boot.events.InitialBootCompleteEvent;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.settings.model.ResolutionSettings;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeOutEvent;
import com.mccurry.game.util.event.EventSubscribers;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class LoadingScreenState extends BaseAppState implements SyncEventHandler<InitialBootCompleteEvent, GameOrchestrationContext> {
    private final Node stateGuiNode = new Node("Loading screen gui node");
    private BitmapText loadingText;
    private Geometry progressBarGeom;
    private final Vector2f loadingProgressMaterialVector = new Vector2f();

    @Override
    protected void initialize(Application application) {
        var baseResolution = ResolutionSettings.R_640x360;

        var simpleApplication = (SimpleApplication) application;

        var orchestrationBus = getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus();
        orchestrationBus.subscribe(InitialBootCompleteEvent.class, this);
        loadingProgressUpdateSubscriber(orchestrationBus, simpleApplication).subscribe(orchestrationBus);

        var guiNode = simpleApplication.getGuiNode();

        var quad = new Quad(baseResolution.getWidth(), baseResolution.getHeight());
        Geometry blackOverlayGeom = new Geometry("BlackQuad", quad);

        var mat = new Material(simpleApplication.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Black);
        blackOverlayGeom.setMaterial(mat);
        stateGuiNode.attachChild(blackOverlayGeom);

        progressBarGeom = createLoadingBarNode(simpleApplication);

        var loadingBarPos = new Vector3f(
                (baseResolution.getWidth()-getProgressBarWidth())*0.5f,
                (baseResolution.getHeight()-getProgressBarHeight())*0.5f,
                1
        );

        progressBarGeom.setLocalTranslation(loadingBarPos);
        stateGuiNode.attachChild(progressBarGeom);


        loadingText = createLoadingText(simpleApplication);

        loadingText.setLocalTranslation(getLoadingTextPos(baseResolution));
        stateGuiNode.attachChild(loadingText);

        stateGuiNode.setLocalTranslation(blackOverlayGeom.getLocalTranslation().clone().setZ(999999f));
        guiNode.attachChild(stateGuiNode);

        System.out.println("[LoadingScreenState] Loading screen initialized");
        orchestrationBus.publish(new BootUiStateSetupCompleteEvent(), new GameOrchestrationContext(), null);
    }

    @Override
    protected void cleanup(Application application) {
        stateGuiNode.removeFromParent();
        stateGuiNode.detachAllChildren();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public void handle(@NonNull InitialBootCompleteEvent event, @NonNull GameOrchestrationContext eventContext, @Nullable EventLifecycleManager<InitialBootCompleteEvent> lifecycleManager) {
        var orchestrationBus = getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus();
        orchestrationBus.unsubscribe(InitialBootCompleteEvent.class, this);
        orchestrationBus.publish(new StartScreenFadeOutEvent(2f), new GameOrchestrationContext(), null);
        System.out.println("[LoadingScreenState] Detaching loading screen.");
        getStateManager().detach(this);
    }

    private BitmapText createLoadingText(SimpleApplication application) {
        var loadingTextFont = application.getAssetManager().loadFont("Interface/Fonts/pixelFlat11.fnt");
        var loadingText = new BitmapText(loadingTextFont);
        loadingText.setSize(loadingTextFont.getCharSet().getRenderedSize() /2);
        loadingText.setText("Loading...");
        return loadingText;
    }


    private Geometry createLoadingBarNode(SimpleApplication app) {
        var assetManager = app.getAssetManager();

        Quad quad = new Quad(getProgressBarWidth(), getProgressBarHeight());
        Geometry geo = new Geometry("OurQuad", quad);
        Material mat = new Material(assetManager, "Shaders/PlayerHealthbar/SimpleColor.j3md");
        var emptyHpBar = assetManager.loadTexture("Textures/GUI/HUD/playerHealthbar/healthbar_background.png");
        var fullWithoutFrame = assetManager.loadTexture("Textures/GUI/HUD/playerHealthbar/healthbar.png");

        emptyHpBar.setMagFilter(Texture.MagFilter.Nearest);
        fullWithoutFrame.setMagFilter(Texture.MagFilter.Nearest);

        mat.setTexture("emptyHealthBar", emptyHpBar);
        mat.setTexture("fullHealthBarWithoutFrame", fullWithoutFrame);
        mat.setVector2("healthPercent", loadingProgressMaterialVector);

        mat.setColor("tint", new ColorRGBA(154f / 255f, 0f / 255f, 0f / 255f, 1f / 255f));
        geo.setMaterial(mat);
        return geo;
    }

    private float getProgressBarWidth() {
        return 128;
    }

    private float getProgressBarHeight() {
        return 19;
    }

    private Vector3f getLoadingTextPos(ResolutionSettings baseResolution){
        return new Vector3f(
                (baseResolution.getWidth()-loadingText.getLineWidth())*0.5f,
                progressBarGeom.getLocalTranslation().clone().subtractLocal(0,loadingText.getHeight(),0).getY(),
                1
        );
    }

    private EventSubscriber<GameOrchestrationContext> loadingProgressUpdateSubscriber(EventBus<GameOrchestrationContext> orchestrationBus, SimpleApplication application){
        return EventSubscribers.syncUnsubscribingOnCondition(
                AssetLoadingPercentAdvanceEvent.class,
                orchestrationBus,
                assetLoadingPercentAdvanceEvent -> {
                    application.enqueue( () -> { // because asset loading happens on other thread
                        var normalizedLoadingPercent = assetLoadingPercentAdvanceEvent.getNormalizedLoadPercent();
                        loadingProgressMaterialVector.setX(normalizedLoadingPercent);
                        if(normalizedLoadingPercent != 1) {
                            loadingText.setText("Loading %s%%".formatted((int) Math.floor(normalizedLoadingPercent * 100)));
                        } else {
                            loadingText.setText("Loading done :D");
                        }
                        loadingText.setLocalTranslation(getLoadingTextPos(ResolutionSettings.R_640x360));
                    });
                },
                assetLoadingPercentAdvanceEvent -> {
                    return assetLoadingPercentAdvanceEvent.getNormalizedLoadPercent() == 1;
                }
        );
    }
}
