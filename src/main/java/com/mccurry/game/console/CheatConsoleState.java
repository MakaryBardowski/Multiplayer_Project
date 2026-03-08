package com.mccurry.game.console;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.mccurry.events.core.EventBus;
import com.mccurry.events.core.EventSubscriber;
import com.mccurry.events.core.handler.SyncEventHandler;
import com.mccurry.game.console.command.CommandRegistry;
import com.mccurry.game.console.event.MainThreadConsoleStateResponseEvent;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import com.mccurry.game.util.event.EventHandlers;
import com.mccurry.game.util.string.JmeRgbaTagsUtil;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TextEntryComponent;
import com.simsilica.lemur.event.KeyAction;
import com.simsilica.lemur.event.KeyActionListener;
import lombok.NonNull;

import static com.mccurry.game.shared.ui.util.ContainerUtil.noBackgroundContainer;

public class CheatConsoleState extends BaseAppState implements EventSubscriber<GameOrchestrationContext> {
    private static final float CONSOLE_INPUT_LINE_SIZE_Y = 12;
    private static final float CONSOLE_SIZE_Y = 150-CONSOLE_INPUT_LINE_SIZE_Y;
    private static final float CONSOLE_MAX_Y = CONSOLE_SIZE_Y+CONSOLE_INPUT_LINE_SIZE_Y;
    private static final float CONSOLE_OPENING_SPEED = 2000;

    private final TextField consoleInputLine;
    private final Container consoleContainer;
    private ConsoleState currentState = ConsoleState.STATIC;

    private final EventBus<GameOrchestrationContext> orchestrationBus;
    private final SyncEventHandler<MainThreadConsoleStateResponseEvent,GameOrchestrationContext> onUiStateResponse;

    public CheatConsoleState(EventBus<GameOrchestrationContext> orchestrationBus){
        this.orchestrationBus = orchestrationBus;

        consoleContainer = noBackgroundContainer();

        var consoleLog = new Label("");
        consoleLog.setInsets(new Insets3f(0,0,0,0));
        consoleLog.setPreferredSize(new Vector3f(640,CONSOLE_SIZE_Y,1));
        consoleLog.setBackground(new QuadBackgroundComponent(ColorRGBA.Gray.clone().setAlpha(0.2f)));
        consoleLog.setColor(ColorRGBA.White);
        consoleLog.setFontSize(8);

        consoleInputLine = new TextField("");
        consoleInputLine.setPreferredSize(new Vector3f(640,CONSOLE_INPUT_LINE_SIZE_Y,1));
        consoleInputLine.setBackground(new QuadBackgroundComponent(ColorRGBA.DarkGray.clone().setAlpha(0.2f)));
        consoleInputLine.setColor(ColorRGBA.White);
        consoleInputLine.setFontSize(8);

        consoleInputLine.getActionMap().put(new KeyAction(KeyInput.KEY_RETURN), new KeyActionListener() {
            @Override
            public void keyAction(TextEntryComponent arg0, KeyAction arg1) {
                var executionResponse = CommandRegistry.execute(orchestrationBus,arg0.getText());
                appendToLog(executionResponse,consoleLog);
                consoleInputLine.setText("");
            }
        });

        consoleContainer.addChild(consoleInputLine);
        consoleContainer.addChild(consoleLog);
        consoleContainer.setPreferredSize(new Vector3f(640,CONSOLE_MAX_Y,1));


        onUiStateResponse = EventHandlers.syncEventHandler(
                (event, ctx, elm)->{
                    appendToLog(JmeRgbaTagsUtil.greenToWhite(event.getStringState()),consoleLog);
                }
        );
    }

    @Override
    public void update(float tpf) {
        if(currentState == ConsoleState.STATIC){
            return;
        }else if (currentState == ConsoleState.OPENING){
            consoleContainer.move(0,CONSOLE_OPENING_SPEED*tpf,0);

            if(consoleContainer.getLocalTranslation().getY() >= CONSOLE_MAX_Y){
                consoleContainer.getLocalTranslation().setY(CONSOLE_MAX_Y);
                currentState = ConsoleState.STATIC;
            }

        }
    }

    @Override
    protected void initialize(Application app) {
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {

        ((SimpleApplication)getApplication()).getGuiNode().attachChild(consoleContainer);
        currentState = ConsoleState.OPENING;
        consoleContainer.setLocalTranslation(0,0,1000555f);
        consoleInputLine.setText("");
        GuiGlobals.getInstance().requestFocus(consoleInputLine);

        subscribe(orchestrationBus);
    }

    @Override
    protected void onDisable() {
        unsubscribe(orchestrationBus);
        consoleContainer.removeFromParent();
    }

    @Override
    public void subscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.subscribe(MainThreadConsoleStateResponseEvent.class, onUiStateResponse);
    }

    @Override
    public void unsubscribe(@NonNull EventBus<GameOrchestrationContext> eventBus) {
        eventBus.unsubscribe(MainThreadConsoleStateResponseEvent.class, onUiStateResponse);
    }

    private enum ConsoleState{
        OPENING,
        STATIC
    }

    private void appendToLog(String executionResponse, Label consoleLog){
        consoleLog.setText(executionResponse+"\n"+consoleLog.getText());
        System.err.println(executionResponse);
    }
}
