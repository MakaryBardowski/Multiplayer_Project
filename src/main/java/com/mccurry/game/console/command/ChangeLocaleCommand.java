package com.mccurry.game.console.command;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.console.exception.CommandParsingException;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import com.mccurry.game.locale.event.MainThreadLocaleChangedEvent;
import com.mccurry.game.locale.model.GameLocale;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class ChangeLocaleCommand extends ConsoleCommand{
    @Override
    public boolean showOnHelpCommand() {
        return true;
    }

    @Override
    public @NonNull String commandName() {
        return "changelocale";
    }

    @Override
    public Object[] parseExecutionArgs(String trimmedArgumentsPart) throws CommandParsingException {
        try{
            return new GameLocale[]{GameLocale.valueOf(trimmedArgumentsPart.toUpperCase())};
        }catch (Exception e){
            throw new CommandParsingException("Failed to parse language from passed argument '%s'".formatted(trimmedArgumentsPart));
        }
    }

    @Override
    public @Nullable String run(EventBus<GameOrchestrationContext> orchestrationBus, Object[] executionArgs) {
        var newLocale = GameLocale.valueOf(executionArgs[0].toString());
        orchestrationBus.publish(
                MainThreadLocaleChangedEvent.builder()
                        .newLocale(newLocale)
                        .build(),
                new GameOrchestrationContext(),
                null
        );
        return "Locale changed to "+newLocale;
    }
}
