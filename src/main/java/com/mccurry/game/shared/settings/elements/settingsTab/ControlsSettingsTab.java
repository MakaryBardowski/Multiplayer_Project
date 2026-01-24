package com.mccurry.game.shared.settings.elements.settingsTab;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.mccurry.game.shared.settings.elements.option.OptionKeybindSelector;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.util.ContainerUtil;
import com.simsilica.lemur.Container;

import java.util.ArrayList;
import java.util.List;

import static com.mccurry.game.keyBinding.model.PlayerAction.*;

public class ControlsSettingsTab extends SettingsTab{
    private final List<OptionKeybindSelector> keybindOptionSelectors = new ArrayList<>();
    private final Container paddingContainer;

    public ControlsSettingsTab(
            Node guiNode,
            InputManager inputManager,
            AssetManager assetManager
    ) {
        super(assetManager);

        var forwardKeybindOption = new OptionKeybindSelector("Move_Forward", MOVE_FORWARD,guiNode,inputManager,assetManager);
        var backwardKeybindOption = new OptionKeybindSelector("Move_Backward",MOVE_BACKWARD,guiNode,inputManager,assetManager);
        var leftKeybindOption = new OptionKeybindSelector("Move_Left",MOVE_LEFT,guiNode,inputManager,assetManager);
        var rightKeybindOption = new OptionKeybindSelector("Move_Right",MOVE_RIGHT,guiNode,inputManager,assetManager);

        var shootKeybindOption = new OptionKeybindSelector("Attack",ATTACK,guiNode,inputManager,assetManager);
        var interactKeybindOption = new OptionKeybindSelector("Interact",INTERACT,guiNode,inputManager,assetManager);
        var skill1KeybindOption = new OptionKeybindSelector("Skill_1",SKILL_1,guiNode,inputManager,assetManager);
        var skill2KeybindOption = new OptionKeybindSelector("Skill_2",SKILL_2,guiNode,inputManager,assetManager);

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
    public void populateWithSettings(UserSettings temporaryUserSettings) {
        keybindOptionSelectors.forEach(
                s -> s.populateWithSettings(temporaryUserSettings)
        );
    }
}
