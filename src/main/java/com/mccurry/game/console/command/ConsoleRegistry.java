package com.mccurry.game.console.command;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.console.exception.CommandParsingException;
import com.mccurry.game.gameStates.orchestrator.GameOrchestrationContext;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.mccurry.game.util.strings.JmeRgbaTagsUtil.green;
import static com.mccurry.game.util.strings.JmeRgbaTagsUtil.red;

public class ConsoleRegistry {
    private static final Map<String,ConsoleCommand> AVAILABLE_COMMANDS = new LinkedHashMap<>(){{

        var helpCommand = new ConsoleCommand(){
            @Override
            public @NonNull String commandName() {
                return "help";
            }

            @Override
            public Object[] parseExecutionArgs(String trimmedArgumentsPart) throws CommandParsingException {
                return new Object[0];
            }

            @Override
            public @Nullable String run(EventBus<GameOrchestrationContext> orchestrationBus, Object[] executionArgs) {
                var sb = new StringBuilder();
                sb.append("Available commands:");
                AVAILABLE_COMMANDS.keySet().forEach(key -> {
                    sb.append("\n   /");
                    sb.append(key);
                });
                return sb.toString();
            }
        };

        var localeChangeNoSave = new ChangeLocaleCommand();
        put(helpCommand.commandName(), helpCommand);
        put(localeChangeNoSave.commandName(), localeChangeNoSave);
    }};

    public static String execute(EventBus<GameOrchestrationContext> orchestrationBus, String consoleInput){
        if(!consoleInput.startsWith("/")){
            return red("Invalid command. Commands must start with \"/\"");
        }

        var commandName = parseCommandName(consoleInput);
        var executableCommand = AVAILABLE_COMMANDS.get(commandName);

        if(executableCommand == null){
            return red("Invalid command \"%s\". To get a list of available commands, use \"/help\"".formatted(commandName));
        }

        var argumentsInput = consoleInput.replace("/"+commandName,"").trim();

        Object[] commandArgs;
        try {
            commandArgs = executableCommand.parseExecutionArgs(argumentsInput);
        } catch (CommandParsingException commandParsingException){
            return red(commandParsingException.getMessage());
        }

        try{
            var execOutput = executableCommand.run(orchestrationBus,commandArgs);
            return green(execOutput == null ? "Command executed successfully" : execOutput);
        } catch (Exception e){
            return red("Exception during command execution: "+e.getMessage());
        }
    }

    private static String parseCommandName(String consoleInput){
        if(consoleInput.contains(" ")){
            return consoleInput.substring(1, consoleInput.indexOf(" "));
        }

        return consoleInput.substring(1);
    }
}
