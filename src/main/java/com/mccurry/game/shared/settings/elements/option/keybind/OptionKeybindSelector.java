package com.mccurry.game.shared.settings.elements.option.keybind;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.mccurry.game.keyBinding.model.KeyBind;
import com.mccurry.game.keyBinding.model.PlayerAction;
import com.mccurry.game.shared.settings.elements.UserSettingsPopulated;
import com.mccurry.game.shared.settings.elements.option.OptionSelector;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.util.FontUtil;
import com.mccurry.game.shared.ui.util.TextureUtil;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import lombok.Getter;

public class OptionKeybindSelector extends OptionSelector implements UserSettingsPopulated {
    private final Button bindButton;

    private UserSettings temporaryUserSettings;

    @Getter
    private final PlayerAction managedPlayerAction;

    public OptionKeybindSelector(
            String optionName,
            PlayerAction playerAction,
            Node guiNode,
            InputManager inputManager,
            AssetManager assetManager) {
        super(optionName, assetManager);
        var font = FontUtil.loadPixelPerfectFont(assetManager, "Interface/Fonts/settings/settings7px.fnt");
        var background = TextureUtil.loadPixelPerfectTexture(assetManager, "Interface/GUI/Settings/SettingSelectorBackground.png");
//        bindButton = new Button(keyBindingService.getKeyBindForAction(playerAction).getKeyName());
        bindButton = new Button("");
        bindButton.setColor(ColorRGBA.White);
        bindButton.setHighlightColor(ColorRGBA.Red);
        bindButton.setFocusColor(ColorRGBA.Red);
        bindButton.setFont(font);
        bindButton.setBackground(new QuadBackgroundComponent(background));
        bindButton.setInsets(new Insets3f(0, 0, 0, 0));
        bindButton.setTextVAlignment(VAlignment.Center);
        bindButton.setTextHAlignment(HAlignment.Center);


        bindButton.addClickCommands(
                source -> {
                    KeyRebindInputListener.getInstance().registerToManager(this,temporaryUserSettings, inputManager, guiNode);
                    source.setText("Press_a_key...");
                }
        );
        managedPlayerAction = playerAction;

        selectorContainer.addChild(bindButton);
        layout();
    }

    private void layout() {
        bindButton.setPreferredSize(
                new Vector3f(
                        MAX_SELECTOR_SIZE_X,
                        12,
                        1
                )
        );
    }

    public void setDisplayedBind(KeyBind bind) {
        var keyName = bind.getKeyDisplayName();
        bindButton.setText(keyName);
    }

    @Override
    public void populateWithSettings(UserSettings temporaryUserSettings) {
        this.temporaryUserSettings = temporaryUserSettings;
        setDisplayedBind(temporaryUserSettings.getKeyBinds().get(managedPlayerAction));
    }
}
