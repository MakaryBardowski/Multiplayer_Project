package com.mccurry.game.shared.settings.elements;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;
import com.mccurry.events.core.EventBus;
import com.mccurry.game.Main;
import com.mccurry.game.gameStates.menu.settings.UserSettingsMenuState;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import com.mccurry.game.gameStates.orchestrator.GameStateOrchestrator;
import com.mccurry.game.shared.settings.elements.settingsTab.ControlsSettingsTab;
import com.mccurry.game.shared.settings.elements.settingsTab.GraphicsSettingsTab;
import com.mccurry.game.shared.settings.elements.settingsTab.SettingsTab;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.util.FontUtil;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;

import java.util.ArrayList;
import java.util.List;

import static com.mccurry.game.shared.ui.util.ContainerUtil.noBackgroundContainer;
import static com.mccurry.game.shared.ui.util.TextureUtil.toGrayscale;

public class UserSettingsPanel extends Container implements UserSettingsPopulated {

    private SettingsTab currentSelectedOptionsTab;

    private final Label settingsLabel;

    private final Container headerContainer;
    private final Container settingsButtonsContainer;

    private final List<Button> optionTabButtons;
    private final GraphicsSettingsTab graphicsSettingsTab;
    private final ControlsSettingsTab controlsSettingsTab;

    private final Button cancelBtn;
    private final Button applyBtn;
    private final Container spaceBetweenActionButtons = noBackgroundContainer();

    public UserSettingsPanel(
            EventBus<GameOrchestrationContext> orchestrationBus,
            AppStateManager appStateManager,
            AssetManager assetManager
    ) {
        this.setLayout(new BoxLayout(Axis.Y,FillMode.None));
        var buttonTexture = assetManager.loadTexture("Interface/GUI/Settings/SettingsPanel.png");

        buttonTexture.setMagFilter(Texture.MagFilter.Nearest);
        buttonTexture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        var backgroundComponent = new QuadBackgroundComponent();
        backgroundComponent.setTexture(buttonTexture);
        this.setBackground(backgroundComponent);

        var headerFont = FontUtil.loadPixelPerfectFont(assetManager, "Interface/Fonts/pixel20.fnt");
        var textColor = ColorRGBA.White.clone();

        headerContainer = noBackgroundContainer();

        settingsLabel = new Label("Settings");
        settingsLabel.setTextHAlignment(HAlignment.Center);
        settingsLabel.setTextVAlignment(VAlignment.Center);

        headerContainer.addChild(settingsLabel);

        settingsLabel.setFont(headerFont);
        settingsLabel.setFontSize(settingsLabel.getFontSize()*0.7f); // todo: remove float scaling

        settingsLabel.setColor(textColor);


        var settingTabContainer = noBackgroundContainer();

        graphicsSettingsTab = new GraphicsSettingsTab(
                orchestrationBus,
                assetManager
        );
        controlsSettingsTab = new ControlsSettingsTab(
                appStateManager.getState(GameStateOrchestrator.class).getKeyBindingService(),
                ((Main)appStateManager.getApplication()).getGuiNode(),
                appStateManager.getApplication().getInputManager(),
                assetManager
        );

        settingsButtonsContainer = noBackgroundContainer();

        settingsButtonsContainer.setLayout(new BoxLayout(Axis.X, FillMode.None));


        var graphicsBtn = settingsTabButton("Graphics",assetManager,true,textColor);
        graphicsBtn.addClickCommands(new Command<Button>() {
                                         @Override
                                         public void execute(Button source) {
                                             if (currentSelectedOptionsTab != null && currentSelectedOptionsTab != graphicsSettingsTab) {
                                                 currentSelectedOptionsTab.removeFromParent();
                                             }
                                             currentSelectedOptionsTab = graphicsSettingsTab;
                                             settingTabContainer.addChild(currentSelectedOptionsTab);
                                             source.setColor(ColorRGBA.Red);
                                             optionTabButtons.stream().filter(btn -> btn != source).forEach(btn -> btn.setColor(ColorRGBA.White));
                                         }
                                     }
        );

        var soundBtn = settingsTabButton("Sound",assetManager,false,textColor);
        var gameplayBtn = settingsTabButton("Gameplay",assetManager,false,textColor);

        var controlsBtn = settingsTabButton("Controls",assetManager,true,textColor);
        controlsBtn.addClickCommands(new Command<Button>() {
                                         @Override
                                         public void execute(Button source) {
                                             if (currentSelectedOptionsTab != null && currentSelectedOptionsTab != controlsSettingsTab) {
                                                 currentSelectedOptionsTab.removeFromParent();
                                             }
                                             currentSelectedOptionsTab = controlsSettingsTab;
                                             settingTabContainer.addChild(currentSelectedOptionsTab);
                                             source.setColor(ColorRGBA.Red);
                                             optionTabButtons.stream().filter(btn -> btn != source).forEach(btn -> btn.setColor(ColorRGBA.White));
                                         }
                                     }
        );

        optionTabButtons = new ArrayList<Button>();
        optionTabButtons.add(graphicsBtn);
        optionTabButtons.add(soundBtn);
        optionTabButtons.add(gameplayBtn);
        optionTabButtons.add(controlsBtn);

        graphicsBtn.click();

        this.addChild(headerContainer);
        this.addChild(settingsButtonsContainer);

        this.addChild(settingTabContainer);

        var actionButtonsContainer = noBackgroundContainer();
        actionButtonsContainer.setLayout(new BoxLayout(Axis.X, FillMode.Even));

        cancelBtn = settingsTabButton("Cancel",assetManager,true,textColor);
        cancelBtn.addClickCommands(source -> {
            appStateManager.detach(appStateManager.getState(UserSettingsMenuState.class));
        });
        applyBtn = settingsTabButton("Apply",assetManager,false,textColor);

        actionButtonsContainer.addChild(cancelBtn);
        actionButtonsContainer.addChild(spaceBetweenActionButtons);
        actionButtonsContainer.addChild(applyBtn);

        this.addChild(actionButtonsContainer);

        layout();
    }


    private void layout() {
        var size = new Vector3f(360, 180, 1);
        this.setPreferredSize(size);

        headerContainer.setPreferredSize(
                new Vector3f(360, 27, 1)
        );


        settingsButtonsContainer.setPreferredSize(
                new Vector3f(360, 18, 1)
        );

        optionTabButtons.forEach(b -> {
            b.setPreferredSize(
                    new Vector3f(90,18,1)
            );
        });

        spaceBetweenActionButtons.setPreferredSize(
                new Vector3f(360-2*90,18,1)
        );

        cancelBtn.setPreferredSize(
                new Vector3f(90,18,1)
        );

        applyBtn.setPreferredSize(
                new Vector3f(90,18,1)
        );
    }


    private Button settingsTabButton(String label, AssetManager assetManager, boolean enabled, ColorRGBA textColor) {

        var button = new Button(label);
        button.setInsets(new Insets3f(0,0,0,0));
        var buttonTexture = assetManager.loadTexture("Interface/GUI/Settings/SettingsTabButton.png");

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


        var buttonFont = FontUtil.loadPixelPerfectFont(assetManager, "Interface/Fonts/settings/settings7px.fnt");
        button.setLocalTranslation(0, 0, 1);

        button.setTextHAlignment(HAlignment.Center);
        button.setTextVAlignment(VAlignment.Center);

        button.setFont(buttonFont);

        button.setFocusColor(ColorRGBA.Red.clone());
        button.setHighlightColor(ColorRGBA.Red.clone());
        button.setColor(textColor);
        settingsButtonsContainer.addChild(button);

        return button;
    }


    @Override
    public void populateWithSettings(UserSettings userSettings) {
        graphicsSettingsTab.populateWithSettings(userSettings);
        controlsSettingsTab.populateWithSettings(userSettings);
    }
}
