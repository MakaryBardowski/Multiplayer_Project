package com.mccurry.game.keyBinding.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode
@RequiredArgsConstructor
public class KeyBind {
    private final TriggerType type;
    private final int keyCode;

    public enum TriggerType {
        MOUSE_BUTTON,
        KEYBOARD
    }

    @JsonIgnore
    public Trigger getTrigger(){
        return type == TriggerType.MOUSE_BUTTON ? new MouseButtonTrigger(keyCode) : new KeyTrigger(keyCode);
    }

    @JsonIgnore
    public String getKeyDisplayName() {
        if(type.equals(TriggerType.KEYBOARD)) {
            switch (keyCode) {
                case KeyInput.KEY_A:
                    return "A";
                case KeyInput.KEY_B:
                    return "B";
                case KeyInput.KEY_C:
                    return "C";
                case KeyInput.KEY_D:
                    return "D";
                case KeyInput.KEY_E:
                    return "E";
                case KeyInput.KEY_F:
                    return "F";
                case KeyInput.KEY_G:
                    return "G";
                case KeyInput.KEY_H:
                    return "H";
                case KeyInput.KEY_I:
                    return "I";
                case KeyInput.KEY_J:
                    return "J";
                case KeyInput.KEY_K:
                    return "K";
                case KeyInput.KEY_L:
                    return "L";
                case KeyInput.KEY_M:
                    return "M";
                case KeyInput.KEY_N:
                    return "N";
                case KeyInput.KEY_O:
                    return "O";
                case KeyInput.KEY_P:
                    return "P";
                case KeyInput.KEY_Q:
                    return "Q";
                case KeyInput.KEY_R:
                    return "R";
                case KeyInput.KEY_S:
                    return "S";
                case KeyInput.KEY_T:
                    return "T";
                case KeyInput.KEY_U:
                    return "U";
                case KeyInput.KEY_V:
                    return "V";
                case KeyInput.KEY_W:
                    return "W";
                case KeyInput.KEY_X:
                    return "X";
                case KeyInput.KEY_Y:
                    return "Y";
                case KeyInput.KEY_Z:
                    return "Z";
                case KeyInput.KEY_0:
                    return "0";
                case KeyInput.KEY_1:
                    return "1";
                case KeyInput.KEY_2:
                    return "2";
                case KeyInput.KEY_3:
                    return "3";
                case KeyInput.KEY_4:
                    return "4";
                case KeyInput.KEY_5:
                    return "5";
                case KeyInput.KEY_6:
                    return "6";
                case KeyInput.KEY_7:
                    return "7";
                case KeyInput.KEY_8:
                    return "8";
                case KeyInput.KEY_9:
                    return "9";
                case KeyInput.KEY_LCONTROL:
                    return "L_Control";
                case KeyInput.KEY_RCONTROL:
                    return "R_Control";
                case KeyInput.KEY_LSHIFT:
                    return "L_Shift";
                case KeyInput.KEY_RSHIFT:
                    return "R_Shift";
            }
        } else {
            switch (keyCode){
                case MouseInput.BUTTON_LEFT -> {
                    return "L_Mouse_Button";
                } case MouseInput.BUTTON_RIGHT -> {
                    return "R_Mouse_Button";
                } case MouseInput.BUTTON_MIDDLE -> {
                    return "M_Mouse_Button";
                }
            }
        }

        return "?";
    }
}