package com.mccurry.game.console.command;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.console.exception.CommandParsingException;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConsoleCommand {

    /**
     * @return the prefix of the console input that will trigger execution.
     * for example: "disableGuns" will make this command run when player inputs "/disableGuns" in the console
     * if given command name is already registered, the command will not be registered
     */
    public abstract @NonNull String commandName();

    /**
     * if parsing fails, {@link com.mccurry.game.console.exception.CommandParsingException} should be thrown.
     * @return arguments for execution, passed to directly to run methods
     */
    public abstract Object[] parseExecutionArgs(String trimmedArgumentsPart) throws CommandParsingException;

    /**
     * always ran from jme main thread, hence its safe to perform scene graph manipulation.
     * its safe to throw an exception in this method
     * @param executionArgs execution args passed from parseExecutionArgs
     * @return statement printed to console. If null, default will be printed.
     */
    public abstract @Nullable String run(EventBus<GameOrchestrationContext> orchestrationBus, Object[] executionArgs);

    @Override
    public int hashCode() {
        return commandName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ConsoleCommand otherCommand)){
            return false;
        }

        return commandName().equals(otherCommand.commandName());
    }
}
