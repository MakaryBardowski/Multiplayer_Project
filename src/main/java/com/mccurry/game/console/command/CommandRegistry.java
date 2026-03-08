package com.mccurry.game.console.command;

import com.mccurry.events.core.EventBus;
import com.mccurry.game.console.exception.CommandParsingException;
import com.mccurry.game.orchestrator.GameOrchestrationContext;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.mccurry.game.util.string.JmeRgbaTagsUtil.greenToWhite;
import static com.mccurry.game.util.string.JmeRgbaTagsUtil.redToWhite;

public class CommandRegistry {
    private static final Map<String,ConsoleCommand> AVAILABLE_COMMANDS = new LinkedHashMap<>(){{

        var helpCommand = new ConsoleCommand(){
            @Override
            public boolean showOnHelpCommand() {
                return false;
            }

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
                AVAILABLE_COMMANDS.entrySet().forEach(commandEntry -> {
                    if(commandEntry.getValue().showOnHelpCommand()){
                        sb.append("\n   /");
                        sb.append(commandEntry.getKey());
                    }
                });
                return sb.toString();
            }
        };

        var localeChangeNoSave = new ChangeLocaleCommand();
        var showUiStateCommand = new ShowUiStateCommand();
        put(helpCommand.commandName(), helpCommand);
        put(showUiStateCommand.commandName(),showUiStateCommand);
        put(localeChangeNoSave.commandName(), localeChangeNoSave);
    }};

    public static String execute(EventBus<GameOrchestrationContext> orchestrationBus, String consoleInput){
        if(!consoleInput.startsWith("/")){
            return redToWhite("Invalid command. Commands must start with \"/\"");
        }

        var commandName = parseCommandName(consoleInput);
        var executableCommand = AVAILABLE_COMMANDS.get(commandName);

        if(executableCommand == null){
            return redToWhite("Invalid command \"%s\". To get a list of available commands, use \"/help\"".formatted(commandName));
        }

        var argumentsInput = consoleInput.replace("/"+commandName,"").trim();

        Object[] commandArgs;
        try {
            commandArgs = executableCommand.parseExecutionArgs(argumentsInput);
        } catch (CommandParsingException commandParsingException){
            return redToWhite(commandParsingException.getMessage());
        }

        try{
            var execOutput = executableCommand.run(orchestrationBus,commandArgs);
            return greenToWhite(execOutput == null ? "Command executed successfully" : execOutput);
        } catch (Exception e){
            return redToWhite("Exception during command execution: "+e.getMessage());
        }
    }

    private static String parseCommandName(String consoleInput){
        if(consoleInput.contains(" ")){
            return consoleInput.substring(1, consoleInput.indexOf(" "));
        }

        return consoleInput.substring(1);
    }
}
