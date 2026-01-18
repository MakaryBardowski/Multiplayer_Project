package com.mccurry.game.gameStates.keyBinding;

import com.mccurry.game.gameStates.keyBinding.model.KeyBind;
import com.mccurry.game.gameStates.keyBinding.model.PlayerAction;
import com.mccurry.game.shared.settings.model.UserSettings;

import java.util.HashMap;
import java.util.Map;

public class KeyBindingService {
    private Map<PlayerAction, KeyBind> currentKeyBinds;

   public static KeyBindingService withKeybindsFromSettings(UserSettings userSettings){
       return new KeyBindingService(userSettings);
   }

    private KeyBindingService(UserSettings userSettings){
        currentKeyBinds = new HashMap<>();
        currentKeyBinds.put(PlayerAction.MOVE_FORWARD,new KeyBind("W","W"));
        currentKeyBinds.put(PlayerAction.MOVE_BACKWARD,new KeyBind("S","S"));
        currentKeyBinds.put(PlayerAction.MOVE_LEFT,new KeyBind("A","A"));
        currentKeyBinds.put(PlayerAction.MOVE_RIGHT,new KeyBind("D","D"));
        currentKeyBinds.put(PlayerAction.ATTACK,new KeyBind("LMB","Mouse_L"));
        currentKeyBinds.put(PlayerAction.INTERACT,new KeyBind("E","E"));
        currentKeyBinds.put(PlayerAction.SKILL_1,new KeyBind("Q","Q"));
        currentKeyBinds.put(PlayerAction.SKILL_2,new KeyBind("R","R"));

    }

    public KeyBind getKeyBindForAction(PlayerAction action){
        return currentKeyBinds.get(action);
    }
}
