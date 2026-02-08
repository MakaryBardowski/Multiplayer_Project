package com.mccurry.game.menu.gamemodeSelector;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.locale.LocalizedString;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.settings.model.ResolutionSettings;
import com.mccurry.game.shared.ui.MenuState;
import com.mccurry.game.sound.event.PlayGlobalSoundRequestEvent;
import com.mccurry.game.sound.model.global.GlobalSoundEntry;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.jetbrains.annotations.Nullable;

import static com.mccurry.game.shared.ui.util.TextureUtil.toGrayscale;

public class GamemodeSelectorState extends MenuState {
    private AssetManager assetManager;

    private EventBus<GameOrchestrationContext> orchestrationBus;

    @Override
    public void createUi(SimpleApplication application, Node stateGuiNode) {
        orchestrationBus = application.getStateManager().getState(GameStateOrchestrator.class).getGameOrchestrationBus();
        assetManager = application.getAssetManager();

        var singlePlayer = newGamemodeButton("GAMEMODE_SINGLE_PLAYER", null, true);
        var hostMultiplayer = newGamemodeButton("GAMEMODE_MULTIPLAYER_HOST", null, false);
        var joinMultiplayer = newGamemodeButton("GAMEMODE_MULTIPLAYER_CLIENT", null, false);

        var baseResolution = ResolutionSettings.R_640x360;
        var buttonSize = singlePlayer.getPreferredSize();
        var buttonX = ((int)(baseResolution.getWidth() * 0.98f)) - buttonSize.getX();


        singlePlayer.setLocalTranslation(new Vector3f(buttonX, 240,1));
        joinMultiplayer.setLocalTranslation(new Vector3f(buttonX, 190,1));
        hostMultiplayer.setLocalTranslation(new Vector3f(buttonX, 140,1));


        stateGuiNode.attachChild(singlePlayer);
        stateGuiNode.attachChild(hostMultiplayer);
        stateGuiNode.attachChild(joinMultiplayer);
    }


    private Button newGamemodeButton(
            String localeKey,
            @Nullable Command<Button> onClick,
            boolean enabled
    ) {
        var button = new Button(LocalizedString.get(localeKey));
        button.setPreferredSize(new Vector3f(120, 36, 1));
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

        Command<Button> defaultClickCommand;

        if (onClick != null) {
            defaultClickCommand = source -> {
                playClickSound();
                onClick.execute(source);
            };
        } else {
            defaultClickCommand = source -> {
                playClickSound();
            };
        }

        button.addClickCommands(defaultClickCommand);


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

    private void playClickSound(){
        orchestrationBus.publish(new PlayGlobalSoundRequestEvent(GlobalSoundEntry.MENU_BUTTON_CLICK),new GameOrchestrationContext(),null);
    }
}
