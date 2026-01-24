package com.mccurry.game.gameStates.menu.main;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.mccurry.game.gameStates.menu.main.event.MainMenuOpened;
import com.mccurry.game.gameStates.menu.settings.UserSettingsMenuState;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.locale.LocalizedString;
import com.mccurry.game.shared.settings.model.ResolutionSettings;
import com.mccurry.game.shared.ui.UiState;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.mccurry.game.gameStates.menu.main.LocaleConstants.*;
import static com.mccurry.game.shared.ui.util.TextureUtil.toGrayscale;

public class MainMenuState extends UiState {
    private static final MainMenuState instance = new MainMenuState();
    private AssetManager assetManager;

    private List<Button> buttons = new ArrayList<>();

    private MainMenuState() {}

    @Override
    public void createUi(SimpleApplication application, Node stateGuiNode) {
        assetManager = application.getAssetManager();
        application.getViewPort().setBackgroundColor(ColorRGBA.Black);
        

        var newGameButton = newMainMenuButton(LOCALE_KEY_NEW_GAME, new Command<Button>() {
            @Override
            public void execute(Button source) {
                System.err.println("button y=%s".formatted(source.getLocalTranslation().getY()));
            }
        }, true);

        var loadGameButton = newMainMenuButton(LOCALE_KEY_LOAD_GAME, null, false);

        var settingsButton = newMainMenuButton(LOCALE_KEY_SETTINGS, new Command<Button>() {
            private UserSettingsMenuState userSettingsMenuState;

            @Override
            public void execute(Button button) {
                if (userSettingsMenuState == null) {
                    userSettingsMenuState = UserSettingsMenuState.getNew();
                }
                getStateManager().attach(userSettingsMenuState);
            }
        }, true);

        var bestiaryButton = newMainMenuButton(LOCALE_KEY_BESTIARY, null, false);

        var quitButton = newMainMenuButton(LOCALE_KEY_QUIT, button -> application.stop(), true);


        buttons.add(newGameButton);
        buttons.add(loadGameButton);
        buttons.add(settingsButton);
        buttons.add(bestiaryButton);
        buttons.add(quitButton);

        buttons.forEach(stateGuiNode::attachChild);

        getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus().publish(
                new MainMenuOpened(),
                new GameOrchestrationContext(),
                null
        );


        layout();
    }

    private void layout(){
        var baseResolution = ResolutionSettings.R_640x360;

        var buttonIndexPlusOne = 1;
        for(var button : buttons){
            var buttonSize = new Vector3f(120, 36, 1);
            button.setPreferredSize(buttonSize);

            Spatial node = button;
            while (node != null) {
                System.err.println(node + " localTrans=" + node.getLocalTranslation() + " scale=" + node.getLocalScale());
                node = node.getParent();
            }

            var screenHeight = baseResolution.getHeight();
            var topButtonMargin = (int)screenHeight * 0.19f;
            var distanceBetweenButtons = (int)buttonSize.getY() * 0.3f;

            var buttonX = ((int)(baseResolution.getWidth() * 0.98f)) - buttonSize.getX();

            button.setLocalTranslation(new Vector3f(
                    buttonX,
                    screenHeight - topButtonMargin -
                            ((buttonIndexPlusOne - 1) * distanceBetweenButtons) -
                            ((buttonIndexPlusOne - 1) * buttonSize.getY()),
                    1
            ));

            buttonIndexPlusOne++;
        }
    }

    private Button newMainMenuButton(
            String localeKey,
            @Nullable Command<Button> onClick,
            boolean enabled
    ) {
        var button = new Button("");
        button.setUserData(METADATA_LOCALE_KEY,localeKey);
        button.setTextVAlignment(VAlignment.Center);
        button.setTextHAlignment(HAlignment.Center);
        button.setHighlightColor(ColorRGBA.Red.clone());
        button.setFocusColor(ColorRGBA.Red.clone());

        var font = assetManager.loadFont("Interface/Fonts/pixelFlat11.fnt");
        Texture fontTex =  font.getPage(0).getTextureParam("ColorMap").getTextureValue();
        fontTex.setMagFilter(Texture.MagFilter.Nearest);
        fontTex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        button.setFont(font);
        button.setColor(ColorRGBA.White.clone());

        if (onClick != null) {
            button.addClickCommands(onClick);
        }

        var buttonTexture = assetManager.loadTexture("Interface/GUI/MainMenu/MainMenuButton.png");

        if (!enabled) {
            button.setColor(ColorRGBA.DarkGray);
            button.setEnabled(false);
            buttonTexture = toGrayscale(buttonTexture);
        }

        buttonTexture.setMagFilter(Texture.MagFilter.Nearest);
        buttonTexture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        var backgroundComponent = new QuadBackgroundComponent();
        backgroundComponent.setTexture(buttonTexture);
        button.setBackground(backgroundComponent);


        return button;
    }

    public static MainMenuState instance() {
        return instance;
    }

    @Override
    public void refresh() {
        buttons.forEach( button -> {
                    var localeKey = (String) button.getUserData(METADATA_LOCALE_KEY);
                    var localizedText = LocalizedString.get(localeKey);
                    button.setText(localizedText);
                }
        );
    }
}
