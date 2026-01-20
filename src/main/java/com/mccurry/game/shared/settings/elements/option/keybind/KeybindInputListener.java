package com.mccurry.game.shared.settings.elements.option.keybind;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.scene.Node;
import com.mccurry.game.gameStates.keyBinding.KeyBindingService;
import com.mccurry.game.shared.settings.elements.option.OptionKeybindSelector;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class KeybindInputListener extends BaseAppState {

    private static final String BINDING_MOUSE_LEFT = "L_Mouse_Button";
    private static final String BINDING_MOUSE_RIGHT = "R_Mouse_Button";
    private static final String BINDING_CTRL_LEFT = "L_Ctrl";
    private static final List<String> NON_ALPHANUMERIC_BINDINGS = List.of(BINDING_MOUSE_LEFT,BINDING_MOUSE_RIGHT,BINDING_CTRL_LEFT);


    private static final String ASCII_ALPHANUMERIC =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final String[] VALID_KEY_BINDINGS = new String[ASCII_ALPHANUMERIC.length()+NON_ALPHANUMERIC_BINDINGS.size()];

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

    @Getter
    private static KeybindInputListener instance;

    public static void initialize(SimpleApplication application){
        instance = new KeybindInputListener(application);
    }

    private final SimpleApplication application;

    private OptionKeybindSelector optionKeybindSelector; // the selector that spawned this rebind listener
    private KeyBindingService keyBindingService;
    private InputManager parentInputManager;
    private Node guiNode;

    @Override
    protected void initialize(Application app) {}

    @Override
    protected void cleanup(Application app) {}

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}

    @Override
    public void update(float tpf) {
        if (optionKeybindSelector != null && !optionKeybindSelector.hasAncestor(guiNode)) {
            removeRebindListener();
            restoreCurrentKeybindText();
            this.getApplication().getStateManager().detach(this);
            return;
        }
    }

    private final ActionListener rebindListener = new ActionListener() {
        @Override
        public void onAction(String mappingName, boolean isPressed, float tpf) {
            if (isValidBinding(mappingName) && isPressed) {

                if (!optionKeybindSelector.hasAncestor(guiNode)) { // this is duplicated to prevent one frame delay between polling and this
                    removeRebindListener();
                    restoreCurrentKeybindText();
                    return;
                }

                var boundPlayerAction = optionKeybindSelector.getManagedPlayerAction();
                var newBind = keyBindingService.bindKeyToAction(boundPlayerAction, mappingName);
                optionKeybindSelector.setDisplayedBind(newBind);
                removeRebindListener();
            }
        }
    };

    private void restoreCurrentKeybindText(){
        var boundPlayerAction = optionKeybindSelector.getManagedPlayerAction();
        var currentBind = keyBindingService.getKeyBindForAction(boundPlayerAction);
        optionKeybindSelector.setDisplayedBind(currentBind);
    }

    private void removeRebindListener(){
        parentInputManager.removeListener(rebindListener);
    }

    /**
     *  MUST BE CALLED FROM MAIN JME THREAD
     */
    public void registerToManager(
            OptionKeybindSelector optionKeybindSelector,
            KeyBindingService keyBindingService,
            InputManager inputManager,
            Node guiNode
    ){
        if(parentInputManager != null && this.optionKeybindSelector != optionKeybindSelector){
            // we can call it without worrying about null pointers because all props are set together
            removeRebindListener();
            restoreCurrentKeybindText();
        }

        this.guiNode = guiNode;
        this.parentInputManager = inputManager;
        this.optionKeybindSelector = optionKeybindSelector;
        this.keyBindingService = keyBindingService;

        inputManager.addMapping(BINDING_MOUSE_LEFT, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("X", new KeyTrigger(KeyInput.KEY_X));

        inputManager.addListener(rebindListener,VALID_KEY_BINDINGS);
        application.getStateManager().attach(this);
    }

    private Boolean isValidBinding(String binding){
        for(var valid : VALID_KEY_BINDINGS){
            if(valid.equals(binding)){
                return true;
            }
        }
        return false;
    }
}
