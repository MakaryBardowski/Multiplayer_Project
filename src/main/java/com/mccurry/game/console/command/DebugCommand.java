package com.mccurry.game.console.command;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.console.event.ToggleHitboxDebugEvent;
import com.mccurry.game.console.event.ToggleRotationDebugEvent;
import com.mccurry.game.console.exception.CommandParsingException;
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
        if(trimmedArgumentsPart.equals("rotation")){
            return new Object[]{
                    DebugType.ROTATION,
                    false
            };
        }
        if(trimmedArgumentsPart.equals("rotation -g")){
            return new Object[]{
                    DebugType.ROTATION,
                    true
            };
        }

        if(trimmedArgumentsPart.equals("hitbox")){
            return new Object[]{
                    DebugType.HITBOX
            };
        }

        throw new CommandParsingException("Invalid debug argument '%s'. Valid values are: ['rotation', 'rotation -g', 'hitbox']".formatted(trimmedArgumentsPart));
    }

    @Override
    public @Nullable String run(EventBus orchestrationBus,@Nullable EventBus gameEventBus,  Object[] executionArgs) {
        if(DebugType.ROTATION.equals(executionArgs[0])){
            if(gameEventBus != null) {
                gameEventBus.publish(new ToggleRotationDebugEvent((boolean)executionArgs[1]), null);
                return "Toggled rotation debug";
            }
            return "Cannot run command. Not currently in game.";
        } else if(DebugType.HITBOX.equals(executionArgs[0])){
            if(gameEventBus != null) {
                gameEventBus.publish(ToggleHitboxDebugEvent.builder().build(), null);
                return "Toggled hitbox debug";
            }
            return "Cannot run command. Not currently in game.";
        }
        return "Congratulations, this should not happen!";
    }

    private enum DebugType{
        ROTATION,
        HITBOX
    }
}
