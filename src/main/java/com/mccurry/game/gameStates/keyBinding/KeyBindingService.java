package com.mccurry.game.gameStates.keyBinding;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.mccurry.game.gameStates.keyBinding.model.KeyBind;
import com.mccurry.game.gameStates.keyBinding.model.PlayerAction;
import com.mccurry.game.shared.settings.model.UserSettings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyBindingService {
    private static final String BINDING_MOUSE_LEFT = "L_Mouse_Button";
    private static final String BINDING_MOUSE_RIGHT = "R_Mouse_Button";
    private static final String BINDING_CTRL_LEFT = "L_Ctrl";
    private static final List<String> NON_ALPHANUMERIC_BINDINGS = List.of(BINDING_MOUSE_LEFT,BINDING_MOUSE_RIGHT,BINDING_CTRL_LEFT);


    private static final String ASCII_ALPHANUMERIC =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String[] VALID_KEY_BINDINGS = new String[ASCII_ALPHANUMERIC.length()+NON_ALPHANUMERIC_BINDINGS.size()];

    static {
        var alphanumericBindings = ASCII_ALPHANUMERIC
                .chars()
                .mapToObj(c -> String.valueOf((char) c))
                .toList();

        int idx;
        for (idx = 0; idx < alphanumericBindings.size(); idx++){
            VALID_KEY_BINDINGS[idx] = alphanumericBindings.get(idx);
        }

        for (int i =0; i < NON_ALPHANUMERIC_BINDINGS.size();i++){
            VALID_KEY_BINDINGS[idx++] = NON_ALPHANUMERIC_BINDINGS.get(i);
        }
    }

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

    public void initializeMappings(InputManager inputManager){


        inputManager.addMapping(BINDING_MOUSE_LEFT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping(BINDING_MOUSE_RIGHT, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping(BINDING_CTRL_LEFT, new KeyTrigger(KeyInput.KEY_LCONTROL));

        inputManager.addMapping("A", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("B", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("C", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("D", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("E", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("F", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("G", new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("H", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("I", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("J", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("K", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("L", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("M", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping("N", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addMapping("O", new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping("P", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Q", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("R", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("S", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("T", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("U", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("V", new KeyTrigger(KeyInput.KEY_V));
        inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("X", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("Y", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("Z", new KeyTrigger(KeyInput.KEY_Z));


        inputManager.addMapping("0", new KeyTrigger(KeyInput.KEY_0),new KeyTrigger(KeyInput.KEY_NUMPAD0));
        inputManager.addMapping("1", new KeyTrigger(KeyInput.KEY_1),new KeyTrigger(KeyInput.KEY_NUMPAD1));
        inputManager.addMapping("2", new KeyTrigger(KeyInput.KEY_2),new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping("3", new KeyTrigger(KeyInput.KEY_3),new KeyTrigger(KeyInput.KEY_NUMPAD3));
        inputManager.addMapping("4", new KeyTrigger(KeyInput.KEY_4),new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping("5", new KeyTrigger(KeyInput.KEY_5),new KeyTrigger(KeyInput.KEY_NUMPAD5));
        inputManager.addMapping("6", new KeyTrigger(KeyInput.KEY_6),new KeyTrigger(KeyInput.KEY_NUMPAD6));
        inputManager.addMapping("7", new KeyTrigger(KeyInput.KEY_7),new KeyTrigger(KeyInput.KEY_NUMPAD7));
        inputManager.addMapping("8", new KeyTrigger(KeyInput.KEY_8),new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addMapping("9", new KeyTrigger(KeyInput.KEY_9),new KeyTrigger(KeyInput.KEY_NUMPAD9));
    }

    public KeyBind getKeyBindForAction(PlayerAction action){
        return currentKeyBinds.get(action);
    }

    public KeyBind bindKeyToAction(PlayerAction playerAction,String rawBind){
       var newBind = new KeyBind("test",rawBind);
       currentKeyBinds.put(playerAction,newBind);
       return newBind;
    }
}
