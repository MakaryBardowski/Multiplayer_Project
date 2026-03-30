package com.mccurry.game.console.command;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.console.event.ToggleRotationDebugEvent;
import com.mccurry.game.console.exception.CommandParsingException;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;


public class DebugCommand extends ConsoleCommand {
    @Override
    public boolean showOnHelpCommand() {
        return true;
    }

    @Override
    public @NonNull String commandName() {
        return "debug";
    }

    @Override
    public Object[] parseExecutionArgs(String trimmedArgumentsPart) throws CommandParsingException {
        if(trimmedArgumentsPart.equals("r")){
            return new Object[]{DebugType.ROTATION};
        }
        throw new CommandParsingException("Invalid debug argument '%s'. Valid values are: ['r']".formatted(trimmedArgumentsPart));
    }

    @Override
    public @Nullable String run(EventBus<GameOrchestrationContext> orchestrationBus, Object[] executionArgs) {
        if(DebugType.ROTATION.equals(executionArgs[0])){
            orchestrationBus.publish(new ToggleRotationDebugEvent(), new GameOrchestrationContext(), null);
            return "Toggled rotation debug";
        }
        return "Congratulations, this should not happen!";
    }

    private enum DebugType{
        ROTATION
    }
}
