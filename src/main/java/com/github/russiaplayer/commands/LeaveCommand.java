package com.github.russiaplayer.commands;

import com.github.russiaplayer.exceptions.NotFoundException;
import com.github.russiaplayer.music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

import static com.github.russiaplayer.bot.MessageSender.getMessageData;
import static com.github.russiaplayer.utils.EventUtils.*;

public class LeaveCommand implements Command {
    @Override
    public void action(SlashCommandInteractionEvent event) {
        try {
            Guild guild = getGuild(event);
            AudioChannelUnion userChannel = getUserChannel(event);
            AudioChannelUnion botChannel = getVoiceState(guild.getSelfMember()).getChannel();

            if (botChannel != null && botChannel != userChannel) {
                event.reply(getMessageData("You are not in the same VoiceChannel")).setEphemeral(true).queue();
                return;
            }
            var musicManager = PlayerManager.getInstance().getMusicManger(guild);

            if (musicManager.audioPlayer.getPlayingTrack() != null) {
                musicManager.audioPlayer.stopTrack();
                musicManager.scheduler.clearQueue();
            }
            guild.getAudioManager().closeAudioConnection();
            event.reply(getMessageData("Ok, I am leaving.")).setEphemeral(true).queue();
        } catch (NotFoundException notFoundException) {
            event.reply(getMessageData(notFoundException.getFriendlyMessage())).setEphemeral(true).queue();
        }
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leave the current voice channel.";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }
}
