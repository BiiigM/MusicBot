package com.github.russiaplayer.bot;

import com.github.russiaplayer.Entity.Server;
import com.github.russiaplayer.SQL.ServerRepo;
import com.github.russiaplayer.commands.Command;
import com.github.russiaplayer.commands.CommandRegistry;
import com.github.russiaplayer.commands.PlayCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.github.russiaplayer.bot.MessageSender.getMessageData;
import static com.github.russiaplayer.bot.MessageSender.sendMessageToMusicChannel;

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
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        Guild guild = event.getGuild();

        Optional<Server> server = SERVER_REPO.getByGuildID(guild.getIdLong());
        if (server.isEmpty()) {
            sendMessageToMusicChannel(guild, "We could not find your server in our List. Pls use the command /setup");
            return;
        }

        TextChannel musicChannel = guild.getTextChannelById(server.get().getChannelId());
        if (musicChannel == null) {
            if (event.getChannelType() == ChannelType.TEXT) {
                event.getChannel().sendMessage(getMessageData("We could not find your music channel. Pls use the command /setup")).queue();
            }
            return;
        }

        event.getMessage().delete().queueAfter(500, TimeUnit.MILLISECONDS);

        ((PlayCommand) commandRegistry.getList().get("play")).action(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        //TODO: button interaction
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
