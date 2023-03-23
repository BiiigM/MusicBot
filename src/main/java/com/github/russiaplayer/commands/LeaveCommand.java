package com.github.russiaplayer.commands;

import com.github.russiaplayer.music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

import static com.github.russiaplayer.bot.MessageSender.getMessageData;

public class LeaveCommand implements Command {
    @Override
    public void action(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();

        if (guild == null) {
            event.reply(getMessageData("You can only use it on a server.")).setEphemeral(true).queue();
            return;
        }

        AudioChannelUnion userChannel = event.getMember().getVoiceState().getChannel();
        AudioChannelUnion botChannel = guild.getSelfMember().getVoiceState().getChannel();

        if (userChannel == null) {
            event.reply(getMessageData("You must join a VoiceChannel")).setEphemeral(true).queue();
            return;
        }

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
