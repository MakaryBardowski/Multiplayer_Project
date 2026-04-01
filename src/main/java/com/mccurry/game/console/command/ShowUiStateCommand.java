package com.mccurry.game.console.command;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.console.event.ConsoleRequestUiStateEvent;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class ShowUiStateCommand extends ConsoleCommand{
    @Override
    public boolean showOnHelpCommand() {
        return false;
    }

    @Override
    public @NonNull String commandName() {
        return "showuistate";
    }

    @Override
    public Object[] parseExecutionArgs(String trimmedArgumentsPart) {
        return new Object[0];
    }

    @Override
    public @Nullable String run(EventBus orchestrationBus, Object[] executionArgs) {
        orchestrationBus.publish(new ConsoleRequestUiStateEvent(), null);
        return null;
    }
}
