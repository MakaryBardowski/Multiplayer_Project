package com.mccurry.game.shared.settings.model;

import lombok.Getter;

@Getter
public enum ResolutionSettings {
    R_640x360(640,360,1),
    R_1280x720(1280,720,2),
    R_1920x1080(1920,1080,3),
    R_2560x1440(2560,1440,4),
    R_3840x2160(3840,2160,5);

    final int width;
    final int height;
    final int uiScale;

    ResolutionSettings(int width, int height, int uiScale){
        this.width = width;
        this.height = height;
        this.uiScale = uiScale;
    }

}
