package com.mccurry.game.shared.settings.elements.option.slider;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.lemur.Slider;
import com.simsilica.lemur.core.VersionedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@NoArgsConstructor
public class SliderValueControl extends AbstractControl {

    private VersionedReference<Double> ref;

    @Getter
    private double exposure;

    private @Nullable Consumer<Slider> onValueChangeCallback;

    public SliderValueControl(@Nullable Consumer<Slider> onValueChangeCallback){
        this.onValueChangeCallback = onValueChangeCallback;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (ref.update()) {
            exposure  =Math.round( getSlider().getModel().getValue() );
            getSlider().getModel().setValue(exposure);
            if(onValueChangeCallback != null){
                onValueChangeCallback.accept(getSlider());
            }
        }
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if(!(spatial instanceof Slider)){
            throw new IllegalArgumentException("Cannot attach SliderValueControl to this spatial");
        }
        ref = ((Slider) spatial).getModel().createReference();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }


    private Slider getSlider() {
        return (Slider) getSpatial();
    }
}
