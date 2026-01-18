package com.mccurry.game.shared.ui.elements;

import com.google.common.base.Function;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.mccurry.game.Main;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.event.MouseListener;
import com.simsilica.lemur.list.SelectionModel;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.value.DefaultValueRenderer;

public class UiScalableSelector<T> extends Selector<T> {

    public UiScalableSelector(VersionedList<T> model, Function<? super T, String> stringTransform, BitmapFont labelFont, Texture background) {
        super(
                true,
                model,
                new ConfigurableDropdownItemRenderer<>((new ElementId("selector")).child("item"), (String) null, stringTransform, labelFont),
                new SelectionModel(),
                new ElementId("selector"),
                (String) null
        );
        this.setBackground(new QuadBackgroundComponent(background));
        this.setInsets(new Insets3f(0,0,0,0));
        this.getExpanderButton().setInsets(new Insets3f(0,0,0,0));
        this.getExpanderButton().setTextHAlignment(HAlignment.Center);
        this.getExpanderButton().setTextVAlignment(VAlignment.Center);
        this.getExpanderButton().setBackground(null);
//        this.getListBox().setInsets(new Insets3f(0,0,0,0)); // causes click detection to break
        this.getExpanderButton().setFont(labelFont);
        this.getExpanderButton().setText("");
    }

    @Override
    protected Vector3f calculatePopupLocation(Vector3f loc) {
        var guiScale = Main.getGuiScale();
        return this.getWorldTranslation().divide(guiScale, guiScale, 1);
    }


    private static class ConfigurableDropdownItemRenderer<T> extends DefaultValueRenderer<T> {
        private final BitmapFont labelFont;

        private ConfigurableDropdownItemRenderer(ElementId elementId, String style, Function<? super T, String> toString, BitmapFont labelFont){
            super(elementId,style,toString);
            this.labelFont = labelFont;
        }

        @Override
        protected String valueToString(T value) {
            return this.getStringTransform() != null ? (String)this.getStringTransform().apply(value) : String.valueOf(value);
        }

        @Override
        protected Label createView(T value, boolean selected) {
            var label = new Label(this.valueToString(value), this.getElementId(), this.getStyle());
            label.setFont(labelFont);
            label.setTextHAlignment(HAlignment.Center);
            label.setTextVAlignment(VAlignment.Center);
            label.setColor(ColorRGBA.White);
            label.addMouseListener(new MouseListener() {
                @Override
                public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {

                }

                @Override
                public void mouseEntered(MouseMotionEvent event, Spatial target, Spatial capture) {
                    label.setColor(ColorRGBA.Red);
                }

                @Override
                public void mouseExited(MouseMotionEvent event, Spatial target, Spatial capture) {
                    label.setColor(ColorRGBA.White);
                }

                @Override
                public void mouseMoved(MouseMotionEvent event, Spatial target, Spatial capture) {

                }
            });
            return label;
        }
    }
}