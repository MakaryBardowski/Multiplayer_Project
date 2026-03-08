package com.mccurry.game.menu.lobby.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SelectablePlayerClass {
    private String selecedIconPath;
    private String iconPath;
    private String name;
    private String description;
}
