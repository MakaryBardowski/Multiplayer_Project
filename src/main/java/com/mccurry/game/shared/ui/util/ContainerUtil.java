package com.mccurry.game.shared.ui.util;

import com.simsilica.lemur.Container;

public class ContainerUtil {
    public static Container noBackgroundContainer(){
        var container = new Container();
        container.setBackground(null);
        return container;
    }
}
