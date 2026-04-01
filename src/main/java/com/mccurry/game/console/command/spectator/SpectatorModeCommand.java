package com.mccurry.game.console.command.spectator;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.console.command.ConsoleCommand;
import com.mccurry.game.console.event.ToggleSpectatorModeEvent;
import com.mccurry.game.console.exception.CommandParsingException;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class SpectatorModeCommand extends ConsoleCommand {
    @Override
    public boolean showOnHelpCommand() {
        return true;
    }

    @Override
    public @NonNull String commandName() {
        return "ghosted";
    }

    @Override
    public Object[] parseExecutionArgs(String trimmedArgumentsPart) throws CommandParsingException {
        return new Object[0];
    }

    @Override
    public @Nullable String run(EventBus orchestrationBus, Object[] executionArgs) {
        orchestrationBus.publish(new ToggleSpectatorModeEvent(), null);
        return "Spectator mode toggled.";
    }
}
