package com.mccurry.game.gameplay.system.hitfeedback.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class HitFeedback {
    @Setter
    @Getter
    private float weight = 1;

    public HitFeedback(){}
}
