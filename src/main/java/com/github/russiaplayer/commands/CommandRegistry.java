package com.github.russiaplayer.commands;

import net.dv8tion.jda.api.JDA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> mapper = new HashMap<>();
    private final JDA jda;

    public CommandRegistry(JDA jda) {
        this.jda = jda;
    }

    public void registerCommand(Command command) {
        mapper.put(command.getName(), command);
        jda.retrieveCommands().queue(commands -> registerNewCommandGlobal(commands, command));
    }

    //Register a command to JDA if it is new
    private void registerNewCommandGlobal(List<net.dv8tion.jda.api.interactions.commands.Command> commands, Command command) {
        if (commands.stream().filter(jdaCommand -> jdaCommand.getName().equals(command.getName())).findFirst().isEmpty()) {
            var commandAction = jda.upsertCommand(command.getName(), command.getDescription());
            if (command.getOptions() == null) {
                commandAction.queue();
                return;
            }
            commandAction.addOptions(command.getOptions()).queue();
        }
    }

    public Map<String, Command> getList() {
        return mapper;
    }
}
