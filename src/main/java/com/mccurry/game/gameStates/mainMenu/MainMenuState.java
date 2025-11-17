package com.mccurry.game.gameStates.mainMenu;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.events.lifecycle.EventLifecycleManager;
import com.mccurry.game.gameStates.mainMenu.events.MainMenuOpened;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.ui.UiState;
import com.mccurry.game.shared.ui.event.MainThreadRefreshUiEvent;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.VAlignment;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class MainMenuState extends UiState {
    private AssetManager assetManager;

    private static final SyncEventHandler<MainThreadRefreshUiEvent,GameOrchestrationContext> RECALCULATE_UI_HANDLER = new SyncEventHandler<MainThreadRefreshUiEvent, GameOrchestrationContext>() {
        @Override
        public void handle(@NonNull MainThreadRefreshUiEvent event, @NonNull GameOrchestrationContext eventContext, @Nullable EventLifecycleManager<MainThreadRefreshUiEvent> lifecycleManager) {
            var uiResolutionSettings = getResolutionSettingsFor(event.getUserSettingsCopy());
        }
    };

    public MainMenuState(){}

    @Override
    protected void doInitialize(SimpleApplication application, Node stateGuiNode, UiResolutionSettings uiResolutionSettings) {
        assetManager = application.getAssetManager();
        application.getViewPort().setBackgroundColor(ColorRGBA.DarkGray);

        stateGuiNode.attachChild(button("New Game",uiResolutionSettings,1,null,true));
        stateGuiNode.attachChild(button("Load Game",uiResolutionSettings,2,null,false));
        stateGuiNode.attachChild(button("Settings",uiResolutionSettings,3,null,true));
        stateGuiNode.attachChild(button("Bestiary",uiResolutionSettings,4,null,false));
        stateGuiNode.attachChild(button("Quit", uiResolutionSettings, 5, new Command<Button>() {
            @Override
            public void execute(Button button) {
                application.stop();
            }
        }, true));

        getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus().publish(
                new MainMenuOpened(),
                new GameOrchestrationContext(),
                null
        );
    }

    @Override
    public void recalculateLayout(float width, float height) {

    }

    @Override
    public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(MainThreadRefreshUiEvent.class, RECALCULATE_UI_HANDLER);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(MainThreadRefreshUiEvent.class, RECALCULATE_UI_HANDLER);
    }

    private Button button(
            String label,
            UiResolutionSettings uiResolutionSettings,
            int buttonOrdinalFromTop,
            @Nullable Command<Button> onClick,
            boolean enabled
    ){
        var button = new Button(label);
        var buttonSize = uiResolutionSettings.getSizeFor(320,60,1);
        button.setPreferredSize(buttonSize);



        button.setTextVAlignment(VAlignment.Center);
        button.setTextHAlignment(HAlignment.Center);
        button.setFontSize(uiResolutionSettings.getBaseUiScale()*6);

        var font = assetManager.loadFont("Interface/Fonts/pixel25.fnt");
        button.setFont(font);

        if(onClick != null){
            button.addClickCommands(onClick);
        }

        if(!enabled){
            button.setEnabled(false);
            button.setAlpha(0.3f,true);
        }

        var screenHeight = uiResolutionSettings.getHeight();
        var topButtonMargin = screenHeight*0.12f;
        var distanceBetweenButtons = buttonSize.getY()*0.3f;


        var buttonX = (uiResolutionSettings.getWidth()*0.98f) - buttonSize.getX();
        button.setLocalTranslation(new Vector3f(
                buttonX,
                screenHeight-topButtonMargin-((buttonOrdinalFromTop-1)*distanceBetweenButtons)-((buttonOrdinalFromTop-1)*buttonSize.getY()),
                1
        ));
        return button;
    }
}
