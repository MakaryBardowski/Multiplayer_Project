package com.mccurry.game.gameplayConfig.keyBinding;

import com.mccurry.game.gameplayConfig.keyBinding.model.KeyBind;
import com.mccurry.game.gameplayConfig.keyBinding.model.PlayerAction;

import java.util.LinkedHashMap;
import java.util.List;

public class KeyBindingRegistry {

    public static final String BINDING_MOUSE_LEFT = "L_Mouse_Button";
    public static final String BINDING_MOUSE_RIGHT = "R_Mouse_Button";
    public static final String BINDING_CTRL_LEFT = "L_Ctrl";
    private static final List<String> NON_ALPHANUMERIC_BINDINGS = List.of(BINDING_MOUSE_LEFT, BINDING_MOUSE_RIGHT, BINDING_CTRL_LEFT);


    private static final String ASCII_ALPHANUMERIC =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String[] VALID_KEY_BINDINGS = new String[ASCII_ALPHANUMERIC.length() + NON_ALPHANUMERIC_BINDINGS.size()];

    static {
        var alphanumericBindings = ASCII_ALPHANUMERIC
                .chars()
                .mapToObj(c -> String.valueOf((char) c))
                .toList();

        int idx;
        for (idx = 0; idx < alphanumericBindings.size(); idx++) {
            VALID_KEY_BINDINGS[idx] = alphanumericBindings.get(idx);
        }

        for (int i = 0; i < NON_ALPHANUMERIC_BINDINGS.size(); i++) {
            VALID_KEY_BINDINGS[idx++] = NON_ALPHANUMERIC_BINDINGS.get(i);
        }
    }

    public static LinkedHashMap<PlayerAction, KeyBind> getNewDefaultBindings(){
        var defaultBinds = new LinkedHashMap<PlayerAction, KeyBind>();
        defaultBinds.put(PlayerAction.MOVE_FORWARD,new KeyBind("W"));
        defaultBinds.put(PlayerAction.MOVE_BACKWARD,new KeyBind("S"));
        defaultBinds.put(PlayerAction.MOVE_LEFT,new KeyBind("A"));
        defaultBinds.put(PlayerAction.MOVE_RIGHT,new KeyBind("D"));
        defaultBinds.put(PlayerAction.ATTACK,new KeyBind(BINDING_MOUSE_LEFT));
        defaultBinds.put(PlayerAction.INTERACT,new KeyBind("E"));
        defaultBinds.put(PlayerAction.SKILL_1,new KeyBind("Q"));
        defaultBinds.put(PlayerAction.SKILL_2,new KeyBind("R"));
        return defaultBinds;
    }
}
