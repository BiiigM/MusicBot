package com.github.russiaplayer.bot;

import com.github.russiaplayer.SQL.ServerSQL;
import com.github.russiaplayer.commands.CommandRegistry;
import com.github.russiaplayer.commands.Setup;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Listener extends ListenerAdapter{
    private final CommandRegistry registry;

    public Listener(CommandRegistry registry){
        this.registry = registry;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;
        var iDs = ServerSQL.getInstance().getIDs(event.getGuild().getIdLong());

        var userMessage = event.getMessage().getContentRaw().toLowerCase(Locale.ROOT);
        var command = registry.getCommand(userMessage);

        if(!(command instanceof Setup) && (iDs.isPresent() && iDs.get().channel() != event.getChannel().getIdLong() || iDs.isEmpty())) {return;}
        event.getMessage().delete().queueAfter(500, TimeUnit.MILLISECONDS);

        if(command == null){
            registry.getCommand("play").action(event);
            return;
        }

        command.action(event);
    }
}
