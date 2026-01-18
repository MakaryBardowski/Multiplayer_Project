package com.mccurry.game.shared.settings.elements.option;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.mccurry.game.shared.ui.util.ContainerUtil;
import com.mccurry.game.shared.ui.util.FontUtil;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.BoxLayout;

public abstract class OptionSelector extends Container {
    private static final int OPTION_CONTAINER_INSETS_TOP_BOTTOM = 1;
    private static final int OPTION_CONTAINER_SIZE_X = 180;
    private static final int OPTION_CONTAINER_INSETS_LEFT = 60;
    protected final int MAX_SELECTOR_SIZE_X = OPTION_CONTAINER_SIZE_X-OPTION_CONTAINER_INSETS_LEFT;

    protected final Label label;
    protected final Container selectorContainer;

    public OptionSelector(String optionName, AssetManager assetManager){
        this.setBackground(null);
        this.setLayout(new BoxLayout(Axis.X, FillMode.Even));

        label = new Label(optionName);
        label.setColor(ColorRGBA.White);
        label.setTextHAlignment(HAlignment.Left);

        var resolutionLabelFont = FontUtil.loadPixelPerfectFont(assetManager,"Interface/Fonts/settings/settings7px.fnt");;
        label.setFont(resolutionLabelFont);

        label.setTextVAlignment(VAlignment.Center);
        label.setTextHAlignment(HAlignment.Left);

        selectorContainer = ContainerUtil.noBackgroundContainer();
        selectorContainer.setInsets(new Insets3f(0,OPTION_CONTAINER_INSETS_LEFT,0,0));

        this.addChild(label);
        this.addChild(selectorContainer);
        layout();
    }

    private void layout() {
        label.setPreferredSize(
                new Vector3f(
                        OPTION_CONTAINER_SIZE_X,
                        12,
                        1
                )
        );
        selectorContainer.setPreferredSize(
                new Vector3f(
                        OPTION_CONTAINER_SIZE_X,
                        12,
                        1
                )
        );
        this.setPreferredSize(new Vector3f(OPTION_CONTAINER_SIZE_X*2,12+(OPTION_CONTAINER_INSETS_TOP_BOTTOM*2),1));
        this.setInsets(new Insets3f(OPTION_CONTAINER_INSETS_TOP_BOTTOM,0,OPTION_CONTAINER_INSETS_TOP_BOTTOM,0));
    }
}
