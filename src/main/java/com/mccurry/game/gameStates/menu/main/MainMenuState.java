package com.mccurry.game.gameStates.menu.main;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import com.mccurry.game.gameStates.menu.main.event.MainMenuOpened;
import com.mccurry.game.gameStates.menu.settings.UserSettingsMenuState;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.ui.UiState;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;
import com.mccurry.game.shared.ui.util.FontUtil;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static com.mccurry.game.shared.ui.util.TextureUtil.toGrayscale;

public class MainMenuState extends UiState {
    private static final MainMenuState instance = new MainMenuState();
    private AssetManager assetManager;

    private List<Button> buttons = new ArrayList<>();

    private MainMenuState() {
    }

    @Override
    public void createUi(SimpleApplication application, Node stateGuiNode, UiResolutionSettings uiResolutionSettings) {
        assetManager = application.getAssetManager();
        application.getViewPort().setBackgroundColor(ColorRGBA.Black);


        var newGameButton = button("New Game", new Command<Button>() {
            @Override
            public void execute(Button source) {
                System.err.println("button y=%s".formatted(source.getLocalTranslation().getY()));
            }
        }, true);

        var loadGameButton = button("Load Game", null, false);

        var settingsButton = button("Settings", new Command<Button>() {
            private UserSettingsMenuState userSettingsMenuState;

            @Override
            public void execute(Button button) {
                if (userSettingsMenuState == null) {
                    userSettingsMenuState = UserSettingsMenuState.getNew();
                }
                getStateManager().attach(userSettingsMenuState);
            }
        }, true);

        var bestiaryButton = button("Bestiary", null, false);

        var quitButton = button("Quit", button -> application.stop(), true);


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
    }

    @Override
    public void layoutUi(UiResolutionSettings uiResolutionSettings) {

        var buttonIndexPlusOne = 1;
        for(var button : buttons){
            var buttonSize = uiResolutionSettings.getSizeFor(120, 36, 1);
            button.setPreferredSize(buttonSize);

            Spatial node = button;
            while (node != null) {
                System.err.println(node + " localTrans=" + node.getLocalTranslation() + " scale=" + node.getLocalScale());
                node = node.getParent();
            }

            var screenHeight = uiResolutionSettings.getHeight();
            var topButtonMargin = (int)screenHeight * 0.19f;
            var distanceBetweenButtons = (int)buttonSize.getY() * 0.3f;
            button.setFontSize(FontUtil.getFontSize(button,uiResolutionSettings));

            var buttonX = ((int)(uiResolutionSettings.getWidth() * 0.98f)) - buttonSize.getX();

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

    private Button button(
            String label,
            @Nullable Command<Button> onClick,
            boolean enabled
    ) {
        var button = new Button(label);
        button.setTextVAlignment(VAlignment.Center);
        button.setTextHAlignment(HAlignment.Center);
        button.setHighlightColor(ColorRGBA.Red.clone());
        button.setFocusColor(ColorRGBA.Red.clone());

        var font = assetManager.loadFont("Interface/Fonts/pixelFlat11.fnt");
        Texture fontTex =  font.getPage(0).getTextureParam("ColorMap").getTextureValue();
        fontTex.setMagFilter(Texture.MagFilter.Nearest);
        fontTex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        button.setFont(font);
        button.setUserData(DATA_KEY_BASE_FONT_SIZE,button.getFontSize());
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

    public static MainMenuState getNew() {
        return instance;
    }
}
