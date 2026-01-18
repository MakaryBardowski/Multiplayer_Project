package com.mccurry.game.shared.settings.elements.option;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameStates.keyBinding.KeyBindingService;
import com.mccurry.game.gameStates.keyBinding.model.PlayerAction;
import com.mccurry.game.shared.ui.util.FontUtil;
import com.mccurry.game.shared.ui.util.TextureUtil;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.QuadBackgroundComponent;

public class OptionKeybindSelector extends OptionSelector {
    private final Button bindButton;

    public OptionKeybindSelector(
            String optionName,
            PlayerAction playerAction,
            KeyBindingService keyBindingService,
            AssetManager assetManager) {
        super(optionName, assetManager);
        var font = FontUtil.loadPixelPerfectFont(assetManager,"Interface/Fonts/settings/settings7px.fnt");
        var background = TextureUtil.loadPixelPerfectTexture(assetManager, "Interface/GUI/Settings/SettingSelectorBackground.png");
        bindButton = new Button(keyBindingService.getKeyBindForAction(playerAction).getUiKeyName());
        bindButton.setColor(ColorRGBA.White);
        bindButton.setHighlightColor(ColorRGBA.Red);
        bindButton.setFocusColor(ColorRGBA.Red);
        bindButton.setFont(font);
        bindButton.setBackground(new QuadBackgroundComponent(background));
        bindButton.setInsets(new Insets3f(0,0,0,0));
        bindButton.setTextVAlignment(VAlignment.Center);
        bindButton.setTextHAlignment(HAlignment.Center);

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
}
