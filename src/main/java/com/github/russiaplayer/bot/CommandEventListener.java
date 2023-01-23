package com.github.russiaplayer.bot;

import com.github.russiaplayer.SQL.ServerRepo;
import com.github.russiaplayer.commands.Command;
import com.github.russiaplayer.commands.CommandRegistry;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandEventListener extends ListenerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(CommandEventListener.class);
    private static final ServerRepo SERVER_REPO = ServerRepo.getInstance();
    private final CommandRegistry commandRegistry;

    public CommandEventListener(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Command command = commandRegistry.getList().get(event.getName());
        if (command == null) {
            LOG.error("Command {} not found", event.getName());
            return;
        }

        command.action(event);
    }

    @Override
    public void onGenericMessage(@NotNull GenericMessageEvent event) {
        //TODO: Play
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Setup setup = new Setup(event.getGuild());
        setup.start();
        LOG.info("Bot joined {} ID: {}", event.getGuild().getName(), event.getGuild().getId());
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        //Deletes the Guild from the database
        long guildId = event.getGuild().getIdLong();
        var server = SERVER_REPO.getByGuildID(guildId);
        if (server.isEmpty()) {
            LOG.error("{} with id {} not found", guildId, event.getGuild().getName());
            return;
        }
        SERVER_REPO.delete(server.get());
    }
}
