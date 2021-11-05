package com.github.russiaplayer.bot;

import com.github.russiaplayer.SQL.ServerIDs;
import com.github.russiaplayer.SQL.ServerSQL;
import com.github.russiaplayer.commands.CommandRegistry;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Listener extends ListenerAdapter{
    private final CommandRegistry registry;
    private final ServerSQL serverDB;

    public Listener(CommandRegistry registry, ServerSQL serverDB){
        this.registry = registry;
        this.serverDB = serverDB;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;
        var iDs = serverDB.getIDs(event.getGuild().getIdLong());
    }
}
