package com.mccurry.game.shared.ui.model;

import com.jme3.math.Vector3f;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UiResolutionSettings {
    private int width;
    private int height;
    private int uiScale;

    public Vector3f getSizeFor(int nominalWidth, int nominalHeight, int z){
        return new Vector3f(
                nominalWidth*uiScale,
                nominalHeight*uiScale,
                z
        );
    }
}
