package com.github.russiaplayer.commands;

import com.github.russiaplayer.bot.Message;
import com.github.russiaplayer.music.PlayerManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Leave implements Command{
    @Override
    public void action(GuildMessageReceivedEvent event) {
        Message message = new Message(event.getGuild());
        var userChannel = event.getMember().getVoiceState().getChannel();
        var botChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();

        if(botChannel == null){
            message.sendNormalMessage(event.getChannel().getIdLong(), "I am not in a VoiceChannel");
            return;
        }

        if(botChannel != userChannel){
            message.sendNormalMessage(event.getChannel().getIdLong(), "You are not in the same VoiceChannel");
            return;
        }

        var musicManager = PlayerManager.getInstance().getMusicManger(event.getGuild());

        if(musicManager.audioPlayer.getPlayingTrack() != null){
            musicManager.audioPlayer.stopTrack();
            musicManager.scheduler.clearQueue();
        }
        event.getGuild().getAudioManager().closeAudioConnection();
    }

    @Override
    public String getHelp() {
        return null;
    }
}
