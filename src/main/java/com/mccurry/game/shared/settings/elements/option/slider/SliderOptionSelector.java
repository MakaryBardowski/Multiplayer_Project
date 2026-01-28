package com.mccurry.game.shared.settings.elements.option.slider;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.mccurry.game.shared.settings.elements.option.OptionSelector;
import com.mccurry.game.shared.ui.util.FontUtil;
import com.mccurry.game.shared.ui.util.TextureUtil;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.core.GuiControl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SliderOptionSelector extends OptionSelector {

    private final Slider slider;

    public SliderOptionSelector(String optionName, Consumer<Slider> onValueChangedCallback, AssetManager assetManager) {
        super(optionName, assetManager);

        slider = new Slider(Axis.X);
        var font = FontUtil.loadPixelPerfectFont(assetManager,"Interface/Fonts/settings/settings7px.fnt");
        slider.getThumbButton().setFont(font);
        slider.getIncrementButton().setFont(font);
        slider.getDecrementButton().setFont(font);

        slider.setInsets(new Insets3f(0,0,0,0));
        slider.getDecrementButton().setInsets(new Insets3f(0,0,0,0));
        slider.getIncrementButton().setInsets(new Insets3f(0,0,0,0));
        slider.getThumbButton().setInsets(new Insets3f(0,0,0,0));
        slider.getRangePanel().setInsets(new Insets3f(0,0,0,0));

        Map<Button.ButtonAction, List<Command<? super Button>>> empty = new HashMap<>();
        slider.getThumbButton().setButtonCommands(empty);
        slider.getThumbButton().setText("");

        slider.getModel().setMinimum(0);
        slider.getModel().setMaximum(100);

        var sliderControl = new SliderValueControl(onValueChangedCallback);
        slider.addControl(sliderControl);


        var buttonBackground = TextureUtil.loadPixelPerfectTexture(assetManager, "Interface/GUI/Settings/Slider/IncrementButton.png");
        slider.getThumbButton().setBackground(new QuadBackgroundComponent(buttonBackground));

        slider.getDecrementButton().setBackground(new QuadBackgroundComponent(buttonBackground));
        slider.getDecrementButton().setTextHAlignment(HAlignment.Center);
        slider.getDecrementButton().setTextVAlignment(VAlignment.Center);
        slider.getDecrementButton().setColor(ColorRGBA.White);

        slider.getIncrementButton().setBackground(new QuadBackgroundComponent(buttonBackground));
        slider.getIncrementButton().setTextHAlignment(HAlignment.Center);
        slider.getIncrementButton().setTextVAlignment(VAlignment.Center);
        slider.getIncrementButton().setColor(ColorRGBA.White);

        slider.setBackground(null);
        var rangePanelBackground = TextureUtil.loadPixelPerfectTexture(assetManager, "Interface/GUI/Settings/Slider/SliderRangePanel.png");
        slider.setBackground(new QuadBackgroundComponent(rangePanelBackground));

        selectorContainer.addChild(slider);
        layout();
    }

    private void layout() {
        slider.setPreferredSize(
                new Vector3f(
                        MAX_SELECTOR_SIZE_X,
                        12,
                        1
                )
        );

        var thumb = slider.getThumbButton();
        thumb.setPreferredSize(new Vector3f(12,10,1));
        thumb.getControl(GuiControl.class).setSize(thumb.getPreferredSize());

        slider.getIncrementButton().setPreferredSize(new Vector3f(12,12,1));
        slider.getDecrementButton().setPreferredSize(new Vector3f(12,12,1));
    }

    public void setValue(double value){
        slider.getModel().setValue(value);
    }
}
