package com.mccurry.game.keyBinding;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.mccurry.game.keyBinding.model.KeyBind;
import com.mccurry.game.keyBinding.model.PlayerAction;

import java.util.*;

import static com.mccurry.game.keyBinding.model.KeyBind.TriggerType.KEYBOARD;
import static com.mccurry.game.keyBinding.model.KeyBind.TriggerType.MOUSE_BUTTON;

public class KeyMappingRegistry {

    public static final String MAPPING_MOUSE_LEFT = "L_Mouse_Button";
    public static final String MAPPING_MOUSE_RIGHT = "R_Mouse_Button";
    public static final String MAPPING_MOUSE_MIDDLE = "M_Mouse_Button";

    public static final String MAPPING_MOUSE_MOVE_X_RIGHT = "R_Mouse_Move_X";
    public static final String MAPPING_MOUSE_MOVE_X_LEFT = "L_Mouse_Move_X";
    public static final String MAPPING_MOUSE_MOVE_Y_UP = "U_Mouse_Move_Y";
    public static final String MAPPING_MOUSE_MOVE_Y_DOWN = "D_Mouse_Move_Y";

    public static final String MAPPING_KEY_A = "A";
    public static final String MAPPING_KEY_B = "B";
    public static final String MAPPING_KEY_C = "C";
    public static final String MAPPING_KEY_D = "D";
    public static final String MAPPING_KEY_E = "E";
    public static final String MAPPING_KEY_F = "F";
    public static final String MAPPING_KEY_G = "G";
    public static final String MAPPING_KEY_H = "H";
    public static final String MAPPING_KEY_I = "I";
    public static final String MAPPING_KEY_J = "J";
    public static final String MAPPING_KEY_K = "K";
    public static final String MAPPING_KEY_L = "L";
    public static final String MAPPING_KEY_M = "M";
    public static final String MAPPING_KEY_N = "N";
    public static final String MAPPING_KEY_O = "O";
    public static final String MAPPING_KEY_P = "P";
    public static final String MAPPING_KEY_Q = "Q";
    public static final String MAPPING_KEY_R = "R";
    public static final String MAPPING_KEY_S = "S";
    public static final String MAPPING_KEY_T = "T";
    public static final String MAPPING_KEY_U = "U";
    public static final String MAPPING_KEY_V = "V";
    public static final String MAPPING_KEY_W = "W";
    public static final String MAPPING_KEY_X = "X";
    public static final String MAPPING_KEY_Y = "Y";
    public static final String MAPPING_KEY_Z = "Z";
    public static final String MAPPING_KEY_0 = "0";
    public static final String MAPPING_KEY_1 = "1";
    public static final String MAPPING_KEY_2 = "2";
    public static final String MAPPING_KEY_3 = "3";
    public static final String MAPPING_KEY_4 = "4";
    public static final String MAPPING_KEY_5 = "5";
    public static final String MAPPING_KEY_6 = "6";
    public static final String MAPPING_KEY_7 = "7";
    public static final String MAPPING_KEY_8 = "8";
    public static final String MAPPING_KEY_9 = "9";

    public static final String MAPPING_KEY_TILDE = "TILDE";

    public static final String MAPPING_CTRL_LEFT = "L_Ctrl";
    public static final String MAPPING_CTRL_RIGHT = "R_Ctrl";

    public static final String MAPPING_SHIFT_LEFT = "L_Shift";
    public static final String MAPPING_SHIFT_RIGHT = "R_Shift";


    private static final Set<Integer> VALID_KEYBOARD_MAPPING_KEYCODES_FOR_REBIND = new HashSet<>(){{
        add(KeyInput.KEY_A);
        add(KeyInput.KEY_B);
        add(KeyInput.KEY_C);
        add(KeyInput.KEY_D);
        add(KeyInput.KEY_E);
        add(KeyInput.KEY_F);
        add(KeyInput.KEY_G);
        add(KeyInput.KEY_H);
        add(KeyInput.KEY_I);
        add(KeyInput.KEY_J);
        add(KeyInput.KEY_K);
        add(KeyInput.KEY_L);
        add(KeyInput.KEY_M);
        add(KeyInput.KEY_N);
        add(KeyInput.KEY_O);
        add(KeyInput.KEY_P);
        add(KeyInput.KEY_Q);
        add(KeyInput.KEY_R);
        add(KeyInput.KEY_S);
        add(KeyInput.KEY_T);
        add(KeyInput.KEY_U);
        add(KeyInput.KEY_V);
        add(KeyInput.KEY_W);
        add(KeyInput.KEY_X);
        add(KeyInput.KEY_Y);
        add(KeyInput.KEY_Z);

        add(KeyInput.KEY_0);
        add(KeyInput.KEY_1);
        add(KeyInput.KEY_2);
        add(KeyInput.KEY_3);
        add(KeyInput.KEY_4);
        add(KeyInput.KEY_5);
        add(KeyInput.KEY_6);
        add(KeyInput.KEY_7);
        add(KeyInput.KEY_8);
        add(KeyInput.KEY_9);

        add(KeyInput.KEY_LCONTROL);
        add(KeyInput.KEY_RCONTROL);

        add(KeyInput.KEY_LSHIFT);
        add(KeyInput.KEY_RSHIFT);
    }};


    private static final Set<Integer> VALID_MOUSE_MAPPING_KEYCODES_FOR_REBIND = new HashSet<>(){{
        add(MouseInput.BUTTON_LEFT);
        add(MouseInput.BUTTON_RIGHT);
        add(MouseInput.BUTTON_MIDDLE);
    }};

    public static LinkedHashMap<PlayerAction, KeyBind> getNewDefaultBindings() {
        var defaultBinds = new LinkedHashMap<PlayerAction, KeyBind>();
        defaultBinds.put(PlayerAction.OPEN_CONSOLE, new KeyBind(KEYBOARD, KeyInput.KEY_GRAVE));

        defaultBinds.put(PlayerAction.MOVE_FORWARD,  new KeyBind(KEYBOARD, KeyInput.KEY_W));
        defaultBinds.put(PlayerAction.MOVE_BACKWARD,  new KeyBind(KEYBOARD, KeyInput.KEY_S));
        defaultBinds.put(PlayerAction.MOVE_LEFT,  new KeyBind(KEYBOARD, KeyInput.KEY_A));
        defaultBinds.put(PlayerAction.MOVE_RIGHT,  new KeyBind(KEYBOARD, KeyInput.KEY_D));

        defaultBinds.put(PlayerAction.ATTACK,  new KeyBind(MOUSE_BUTTON, MouseInput.BUTTON_LEFT));
        defaultBinds.put(PlayerAction.INTERACT,  new KeyBind(KEYBOARD, KeyInput.KEY_E));
        defaultBinds.put(PlayerAction.SKILL_1,  new KeyBind(KEYBOARD, KeyInput.KEY_Q));
        defaultBinds.put(PlayerAction.SKILL_2,  new KeyBind(KEYBOARD, KeyInput.KEY_R));
        return defaultBinds;
    }

    public static boolean isValidKeyboardKeyCode(int keyboardKeyCode){
        return VALID_KEYBOARD_MAPPING_KEYCODES_FOR_REBIND.contains(keyboardKeyCode);
    }

    public static boolean isValidMouseKeyCode(int mouseKeyCode){
        return VALID_MOUSE_MAPPING_KEYCODES_FOR_REBIND.contains(mouseKeyCode);
    }
}
