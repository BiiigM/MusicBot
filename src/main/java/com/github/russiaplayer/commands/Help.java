package com.github.russiaplayer.commands;

import com.github.russiaplayer.bot.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Help implements Command{
    private final CommandRegistry registry;

    public Help(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void action(GuildMessageReceivedEvent event) {
        Message message = new Message(event.getGuild());

        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("**All Commands:**\n");

        //Blacklist: Commands which should not show up at the help Command
        List<String> blackList = new ArrayList<>();
        blackList.add("?");

        //Add Commands to the String
        registry.getList().entrySet().stream()
                .filter(x -> x.getValue().getHelp() != null)
                .filter(x -> !blackList.contains(x.getKey()))
                .forEach(x -> helpMessage.append(x.getKey()).append(" - ").append(x.getValue().getHelp()).append("\n"));

        message.sendNormalMessage(event.getChannel().getIdLong(), helpMessage.toString());
    }

    @Override
    public String getHelp() {
        return "Shows you this Message.";
    }
}
