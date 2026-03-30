package com.mccurry.game.shared.settings.elements.option.keybind;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.*;
import com.jme3.scene.Node;
import com.mccurry.game.keyBinding.model.KeyBind;
import com.mccurry.game.shared.settings.model.UserSettings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.mccurry.game.keyBinding.KeyMappingRegistry.isValidKeyboardKeyCode;
import static com.mccurry.game.keyBinding.KeyMappingRegistry.isValidMouseKeyCode;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class KeyRebindInputListener extends BaseAppState {

    @Getter
    private static KeyRebindInputListener instance;

    public static void initialize(SimpleApplication application){
        instance = new KeyRebindInputListener(application);
    }

    private final SimpleApplication application;

    private OptionKeybindSelector optionKeybindSelector; // the selector that spawned this rebind listener
    private UserSettings temporaryUserSettings;
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

            /* its ok to do it, because if settings.cancel is clicked -> then on next open of settings fresh values are displayed not from temporary settings.
            if settings.apply is clicked -> then this is the fresh value
             */
            restoreKeybindTextFromUncommitedSettings();
            this.getApplication().getStateManager().detach(this);
            return;
        }
    }

    private final RawInputListener rawRebingListener = new RawInputListener() {
        @Override
        public void beginInput() {

        }

        @Override
        public void endInput() {

        }

        @Override
        public void onJoyAxisEvent(JoyAxisEvent evt) {

        }

        @Override
        public void onJoyButtonEvent(JoyButtonEvent evt) {

        }

        @Override
        public void onMouseMotionEvent(MouseMotionEvent evt) {

        }

        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if (evt.isPressed() && isValidMouseKeyCode(evt.getButtonIndex())) {

                if (!optionKeybindSelector.hasAncestor(guiNode)) { // this is duplicated to prevent one frame delay between polling and this
                    removeRebindListener();
                    restoreKeybindTextFromUncommitedSettings();
                    return;
                }

                var boundPlayerAction = optionKeybindSelector.getManagedPlayerAction();
                var newUncommitedBind = new KeyBind(
                        KeyBind.TriggerType.MOUSE_BUTTON, evt.getButtonIndex()
                );
                temporaryUserSettings.getKeyBinds().put(boundPlayerAction,newUncommitedBind);
                optionKeybindSelector.setDisplayedBind(newUncommitedBind);
                removeRebindListener();
            }
        }

        @Override
        public void onKeyEvent(KeyInputEvent evt) {
            if (evt.isPressed() && isValidKeyboardKeyCode(evt.getKeyCode())) {

                if (!optionKeybindSelector.hasAncestor(guiNode)) { // this is duplicated to prevent one frame delay between polling and this
                    removeRebindListener();
                    restoreKeybindTextFromUncommitedSettings();
                    return;
                }

                var boundPlayerAction = optionKeybindSelector.getManagedPlayerAction();
                var newUncommitedBind = new KeyBind(
                        KeyBind.TriggerType.KEYBOARD, evt.getKeyCode()
                );
                temporaryUserSettings.getKeyBinds().put(boundPlayerAction,newUncommitedBind);
                optionKeybindSelector.setDisplayedBind(newUncommitedBind);
                removeRebindListener();
            }
        }

        @Override
        public void onTouchEvent(TouchEvent evt) {

        }
    };

    private void restoreKeybindTextFromUncommitedSettings(){
        var boundPlayerAction = optionKeybindSelector.getManagedPlayerAction();
        var currentBind = temporaryUserSettings.getKeyBinds().get(boundPlayerAction);
        optionKeybindSelector.setDisplayedBind(currentBind);
    }

    private void removeRebindListener(){
        parentInputManager.removeRawInputListener(rawRebingListener);
    }

    /**
     *  MUST BE CALLED FROM MAIN JME THREAD
     */
    public void registerToManager(
            OptionKeybindSelector optionKeybindSelector,
            UserSettings temporaryUserSettings,
            InputManager inputManager,
            Node guiNode
    ){
        if(parentInputManager != null && this.optionKeybindSelector != optionKeybindSelector){
            // we can call it without worrying about null pointers because all props are set together
            removeRebindListener();
            restoreKeybindTextFromUncommitedSettings();
        }

        this.guiNode = guiNode;
        this.parentInputManager = inputManager;
        this.optionKeybindSelector = optionKeybindSelector;
        this.temporaryUserSettings = temporaryUserSettings;

        inputManager.addRawInputListener(rawRebingListener);
        application.getStateManager().attach(this);
    }
}
