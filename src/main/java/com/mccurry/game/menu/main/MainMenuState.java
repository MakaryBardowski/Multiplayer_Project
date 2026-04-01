package com.mccurry.game.menu.main;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.menu.main.event.CloseMenuRequestEvent;
import com.mccurry.game.menu.main.event.MainMenuOpened;
import com.mccurry.game.menu.main.event.OpenMenuRequestEvent;
import com.mccurry.game.orchestrator.GameStateOrchestrator;
import com.mccurry.game.locale.LocalizedString;
import com.mccurry.game.shared.settings.model.ResolutionSettings;
import com.mccurry.game.shared.ui.MenuState;
import com.mccurry.game.shared.ui.MenuType;
import com.mccurry.game.sound.event.PlayGlobalSoundRequestEvent;
import com.mccurry.game.sound.model.global.GlobalSoundEntry;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.mccurry.game.menu.main.LocaleConstants.*;
import static com.mccurry.game.shared.ui.util.TextureUtil.toGrayscale;

@RequiredArgsConstructor
public class MainMenuState extends MenuState {
    private AssetManager assetManager;
    private final List<Button> buttons = new ArrayList<>();

    private final EventBus orchestrationBus;

    @Override
    public void createUiOnce(SimpleApplication application, Node stateGuiNode) {
        assetManager = application.getAssetManager();
        application.getViewPort().setBackgroundColor(ColorRGBA.Black);


        var newGameButton = newMainMenuButton(LOCALE_KEY_NEW_GAME, button -> {
            orchestrationBus.publish(
                    OpenMenuRequestEvent.builder()
                            .menuType(MenuType.GAMEMODE_SELECTOR)
                            .build(),
                    null
            );
        }, true);

        var loadGameButton = newMainMenuButton(LOCALE_KEY_LOAD_GAME, null, false);

        var settingsButton = newMainMenuButton(LOCALE_KEY_SETTINGS, button -> orchestrationBus.publish(
                OpenMenuRequestEvent.builder()
                        .menuType(MenuType.SETTINGS)
                        .build(),
                null
        ), true);

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
                null
        );


        layout();
    }

    private void layout() {
        var baseResolution = ResolutionSettings.R_640x360;

        var buttonIndexPlusOne = 1;
        for (var button : buttons) {
            var buttonSize = button.getPreferredSize();
            Spatial node = button;
            while (node != null) {
                node = node.getParent();
            }

            var screenHeight = baseResolution.getHeight();
            var topButtonMargin = (int) screenHeight * 0.19f;
            var distanceBetweenButtons = (int) buttonSize.getY() * 0.3f;

            var buttonX = ((int) (baseResolution.getWidth() * 0.98f)) - buttonSize.getX();

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
        var button = new Button(LocalizedString.get(localeKey));
        button.setPreferredSize(new Vector3f(120, 36, 1));
        button.setUserData(METADATA_LOCALE_KEY, localeKey);
        button.setTextVAlignment(VAlignment.Center);
        button.setTextHAlignment(HAlignment.Center);
        button.setHighlightColor(ColorRGBA.Red.clone());
        button.setFocusColor(ColorRGBA.Red.clone());

        var font = assetManager.loadFont("Interface/Fonts/pixelFlat11.fnt");
        Texture fontTex = font.getPage(0).getTextureParam("ColorMap").getTextureValue();
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

    public void refreshLocale() {
        buttons.forEach(button -> {
                    var localeKey = (String) button.getUserData(METADATA_LOCALE_KEY);
                    var localizedText = LocalizedString.get(localeKey);
                    button.setText(localizedText);
                }
        );
    }

    private void playClickSound() {
        orchestrationBus.publish(new PlayGlobalSoundRequestEvent(GlobalSoundEntry.MENU_BUTTON_CLICK), null);
    }
}
