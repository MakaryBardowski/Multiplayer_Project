package com.mccurry.game.gameplay.menu.pausemenu;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.gameplay.event.CloseGameEvent;
import com.mccurry.game.gameplay.menu.pausemenu.event.GameplayChangePauseStateRequest;
import com.mccurry.game.gameplay.menu.pausemenu.model.GameplayPauseState;
import com.mccurry.game.locale.LocalizedString;
import com.mccurry.game.menu.main.event.CloseMenuRequestEvent;
import com.mccurry.game.menu.main.event.OpenMenuRequestEvent;
import com.mccurry.game.shared.settings.model.ResolutionSettings;
import com.mccurry.game.shared.ui.MenuState;
import com.mccurry.game.shared.ui.MenuType;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeInEvent;
import com.mccurry.game.shared.ui.transition.events.StartScreenFadeOutEvent;
import com.mccurry.game.sound.event.PlayGlobalSoundRequestEvent;
import com.mccurry.game.sound.model.global.GlobalSoundEntry;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.mccurry.game.shared.ui.util.TextureUtil.toGrayscale;

@RequiredArgsConstructor
public class PauseMenuState extends MenuState {
    private final EventBus orchestrationBus;
    private final List<Button> buttons = new ArrayList<>();
    private Geometry blackBackgroundGeom;

    @Override
    public void createUiOnce(SimpleApplication application, Node stateGuiNode) {
        var continueBtn = newPauseMenuButton(LocaleKeys.CONTINUE, (btn)->{
            orchestrationBus.publish(CloseMenuRequestEvent.builder().menuType(MenuType.PAUSE_MENU).build(), null);
        }, true);
        var settingsBtn = newPauseMenuButton(LocaleKeys.SETTINGS,(btn)->{
            orchestrationBus.publish(
                    OpenMenuRequestEvent.builder()
                            .menuType(MenuType.SETTINGS)
                            .build(),
                    null
            );
        }, true);
        var mainMenuBtn = newPauseMenuButton(LocaleKeys.MAIN_MENU,
                (btn) -> {
                    orchestrationBus.publish(new StartScreenFadeInEvent(1,
                            () -> {
                                orchestrationBus.publish(OpenMenuRequestEvent.builder().menuType(MenuType.MAIN_MENU).build(),null);
                                orchestrationBus.publish( new CloseGameEvent() ,null);
                                orchestrationBus.publish(new StartScreenFadeOutEvent(1), null);
                            }
                    ), null);

                }, true);

        buttons.add(continueBtn);
        buttons.add(settingsBtn);
        buttons.add(mainMenuBtn);

        blackBackgroundGeom = blackBackgroundGeom(ResolutionSettings.R_640x360);
        layout();

        buttons.forEach(stateGuiNode::attachChild);
    }

    private void layout() {

        var baseResolution = ResolutionSettings.R_640x360;
        var screenHeight = baseResolution.getHeight();
        var topButtonMargin = (int) screenHeight * 0.25f;

        var buttonIndexPlusOne = 1;
        for (var button : buttons) {
            var buttonSize = button.getPreferredSize();
            Spatial node = button;
            while (node != null) {
                node = node.getParent();
            }

            var distanceBetweenButtons = (int) buttonSize.getY() * 0.3f;

            var buttonX = ((int) (baseResolution.getWidth() * 0.5f)) - ((int)buttonSize.getX()/2);

            button.setLocalTranslation(new Vector3f(
                    buttonX,
                    screenHeight - topButtonMargin -
                            ((buttonIndexPlusOne - 1) * distanceBetweenButtons) -
                            ((buttonIndexPlusOne - 1) * buttonSize.getY()),
                    2
            ));

            buttonIndexPlusOne++;
        }
    }

    private Geometry blackBackgroundGeom(ResolutionSettings baseResolution){
        var quad = new Quad(baseResolution.getWidth(), baseResolution.getHeight());
        var overlayGeom = new Geometry("BlackQuad", quad);

        var mat = new Material(getApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Black.clone().setAlpha(0.5f));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        overlayGeom.setMaterial(mat);
        overlayGeom.setQueueBucket(RenderQueue.Bucket.Gui);
        overlayGeom.getLocalTranslation().setZ(1);
        return overlayGeom;
    }

    private Button newPauseMenuButton(
            String localeKey,
            @Nullable Command<Button> onClick,
            boolean enabled
    ) {
        var assetManager = getApplication().getAssetManager();
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

    private void playClickSound() {
        orchestrationBus.publish(new PlayGlobalSoundRequestEvent(GlobalSoundEntry.MENU_BUTTON_CLICK), null);
    }

    @Override
    protected void onEnable() {
        getStateGuiNode().attachChild(blackBackgroundGeom);
        super.onEnable();
        orchestrationBus.publish(
                new GameplayChangePauseStateRequest(GameplayPauseState.PAUSED), null
        );
    }

    @Override
    protected void onDisable() {
        var stateGuiParent = getStateGuiNode().getParent();

        if(stateGuiParent != null){
            stateGuiParent.attachChild(blackBackgroundGeom);
        }

        super.onDisable();
    }

    @Override
    protected void cleanup(Application application) {
        super.onDisable();
        orchestrationBus.publish(
                new GameplayChangePauseStateRequest(GameplayPauseState.UNPAUSED), null
        );
        blackBackgroundGeom.removeFromParent();
    }
}
