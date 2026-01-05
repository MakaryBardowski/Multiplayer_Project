package com.mccurry.game.shared.settings.elements.option;

import com.jme3.asset.AssetManager;
import com.mccurry.game.shared.ui.RedrawableElement;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;
import com.mccurry.game.shared.ui.util.FontUtil;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.BoxLayout;

import static com.mccurry.game.shared.ui.UiState.DATA_KEY_BASE_FONT_SIZE;

public abstract class OptionSelector extends Container implements RedrawableElement {
    protected final Label label;

    public OptionSelector(String optionName, AssetManager assetManager){
        this.setBackground(null);
        this.setLayout(new BoxLayout(Axis.X, FillMode.Even));

        label = new Label(optionName);
        label.setTextHAlignment(HAlignment.Left);

        var resolutionLabelFont = FontUtil.loadPixelPerfectFont(assetManager,"Interface/Fonts/settings/settings7px.fnt");;
        label.setFont(resolutionLabelFont);

        label.setUserData(DATA_KEY_BASE_FONT_SIZE, label.getFontSize());

        label.setTextVAlignment(VAlignment.Center);
        label.setTextHAlignment(HAlignment.Left);

        this.addChild(label);
    }

    @Override
    public void resizeForResolution(UiResolutionSettings resolutionSettings) {
        label.setFontSize(
                FontUtil.getFontSize(label,resolutionSettings)
        );

        label.setPreferredSize(
                resolutionSettings.getSizeFor(
                        180,
                        10,
                        1
                )
        );
    }
}
