package com.github.russiaplayer.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface Command {
    void action(GuildMessageReceivedEvent event);
    String getHelp();
}
