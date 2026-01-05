package com.mccurry.game.shared.settings.model;

import lombok.Getter;

@Getter
public enum ResolutionSettings {
    R_640x360(640,360),
    R_1280x720(1280,720),
    R_1600x900(1600,900),
    R_1920x1080(1920,1080),
    R_2560x1440(2560,1440),
    R_3840x2160(3840,2160);

    final int width;
    final int height;

    ResolutionSettings(int width, int height){
        this.width = width;
        this.height = height;
    }

}
