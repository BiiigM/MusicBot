package com.github.russiaplayer.commands;

import com.github.russiaplayer.exceptions.NotFoundException;
import com.github.russiaplayer.music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

import static com.github.russiaplayer.bot.MessageSender.getMessageData;
import static com.github.russiaplayer.utils.EventUtils.*;

public class SkipCommand implements Command {
    @Override
    public void action(SlashCommandInteractionEvent event) {
        try {
            Guild guild = getGuild(event);
            AudioChannelUnion userChannel = getUserChannel(event);
            AudioChannelUnion botChannel = getBotChannel(event);

            skipCurrentTrack(guild, userChannel, botChannel, event);
        } catch (NotFoundException notFoundException) {
            event.reply(getMessageData(notFoundException.getFriendlyMessage())).setEphemeral(true).queue();
        }
    }

    public void action(ButtonInteractionEvent event) {
        try {
            Guild guild = getGuild(event);
            AudioChannelUnion userChannel = getUserChannel(event);
            AudioChannelUnion botChannel = getBotChannel(event);

            skipCurrentTrack(guild, userChannel, botChannel, event);
        } catch (NotFoundException notFoundException) {
            event.reply(getMessageData(notFoundException.getFriendlyMessage())).setEphemeral(true).queue();
        }
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips the current song.";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    private void skipCurrentTrack(Guild guild, AudioChannelUnion userChannel,
                                  AudioChannelUnion botChannel, IReplyCallback event) {
        if (botChannel != null && botChannel != userChannel) {
            event.reply(getMessageData("You are not in the same VoiceChannel")).setEphemeral(true).queue();
            return;
        }

        PlayerManager.getInstance().getMusicManger(guild).scheduler.nextTrack();
        event.reply(getMessageData("Skipping current song.")).setEphemeral(true).queue();
    }
}
