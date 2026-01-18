package com.mccurry.game.shared.settings.elements.settingsTab;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.mccurry.game.gameStates.keyBinding.KeyBindingService;
import com.mccurry.game.shared.settings.elements.option.OptionKeybindSelector;
import com.mccurry.game.shared.settings.elements.option.OptionSelector;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.util.ContainerUtil;
import com.simsilica.lemur.Container;

import java.util.ArrayList;
import java.util.List;

import static com.mccurry.game.gameStates.keyBinding.model.PlayerAction.*;
import static com.mccurry.game.shared.settings.elements.option.OptionSelector.OPTION_CONTAINER_INSETS_TOP_BOTTOM;

public class ControlsSettingsTab extends SettingsTab{
    private List<OptionSelector> keybindOptionSelectors = new ArrayList<>();
    private Container paddingContainer;

    public ControlsSettingsTab(
            KeyBindingService keyBindingService,
            AssetManager assetManager
    ) {
        super(assetManager);

        var forwardKeybindOption = new OptionKeybindSelector("Move_Forward", MOVE_FORWARD,keyBindingService,assetManager);
        var backwardKeybindOption = new OptionKeybindSelector("Move_Backward",MOVE_BACKWARD,keyBindingService,assetManager);
        var leftKeybindOption = new OptionKeybindSelector("Move_Left",MOVE_LEFT,keyBindingService,assetManager);
        var rightKeybindOption = new OptionKeybindSelector("Move_Right",MOVE_RIGHT,keyBindingService,assetManager);

        var shootKeybindOption = new OptionKeybindSelector("Attack",ATTACK,keyBindingService,assetManager);
        var interactKeybindOption = new OptionKeybindSelector("Interact",INTERACT,keyBindingService,assetManager);
        var skill1KeybindOption = new OptionKeybindSelector("Skill_1",SKILL_1,keyBindingService,assetManager);
        var skill2KeybindOption = new OptionKeybindSelector("Skill_2",SKILL_2,keyBindingService,assetManager);

        keybindOptionSelectors.add(forwardKeybindOption);
        keybindOptionSelectors.add(backwardKeybindOption);
        keybindOptionSelectors.add(leftKeybindOption);
        keybindOptionSelectors.add(rightKeybindOption);

        keybindOptionSelectors.add(shootKeybindOption);
        keybindOptionSelectors.add(interactKeybindOption);
        keybindOptionSelectors.add(skill1KeybindOption);
        keybindOptionSelectors.add(skill2KeybindOption);

        keybindOptionSelectors.forEach(this::addChild);

        paddingContainer = ContainerUtil.noBackgroundContainer();
        this.addChild(paddingContainer);
        layout();
    }

    private void layout() {
        var size = new Vector3f(360, 135-18, 1);
        this.setPreferredSize(size);

        var insets = getTabInsets();
        this.setInsets(insets);

        var paddingVerticalOffset = keybindOptionSelectors.stream()
                .map(optionSelector -> optionSelector.getPreferredSize().getY())
                .reduce(0f, Float::sum);

        paddingContainer.setPreferredSize(
                this.getPreferredSize().clone().subtractLocal(
                        0,
                        paddingVerticalOffset +insets.getMinInsets().getY() + insets.getMaxInsets().getY(),
                        0
                )
        );
    }



    @Override
    public void populateWithSettings(UserSettings userSettings) {}
}
