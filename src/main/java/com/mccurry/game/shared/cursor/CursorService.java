package com.mccurry.game.shared.cursor;

import com.simsilica.lemur.GuiGlobals;

public class CursorService {
    public static void cursorEnabled(boolean enabled){
        GuiGlobals.getInstance().setCursorEventsEnabled(enabled);
    }
}
