package com.github.russiaplayer.commands;

import com.github.russiaplayer.bot.Message;
import com.github.russiaplayer.music.PlayerManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class Stop implements Command {
    @Override
    public void action(GuildMessageReceivedEvent event) {
        Message message = new Message(event.getGuild());
        var userChannel = event.getMember().getVoiceState().getChannel();
        var botChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();

        if(userChannel == null){
            message.sendNormalMessage(event.getChannel().getIdLong(), "You must join a VoiceChannel");
            return;
        }
        if(botChannel != null && botChannel != userChannel){
            message.sendNormalMessage(event.getChannel().getIdLong(), "You are not in the same VoiceChannel");
            return;
        }

        var musicManager = PlayerManager.getInstance().getMusicManger(event.getGuild());

        if(musicManager.audioPlayer.getPlayingTrack() == null){
            message.sendNormalMessage(event.getChannel().getIdLong(), "No Song playing");
            return;
        }

        musicManager.audioPlayer.stopTrack();
        musicManager.scheduler.clearQueue();
        message.sendNormalMessage(event.getChannel().getIdLong(), "Stopping Playlist");
    }

    public void actionReaction(GuildMessageReactionAddEvent event){
        Message message = new Message(event.getGuild());
        var userChannel = event.getMember().getVoiceState().getChannel();
        var botChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();

        if(userChannel == null){
            message.sendNormalMessage(event.getChannel().getIdLong(), "You must join a VoiceChannel");
            return;
        }
        if(botChannel != null && botChannel != userChannel){
            message.sendNormalMessage(event.getChannel().getIdLong(), "You are not in the same VoiceChannel");
            return;
        }

        var musicManager = PlayerManager.getInstance().getMusicManger(event.getGuild());

        if(musicManager.audioPlayer.getPlayingTrack() == null){
            message.sendNormalMessage(event.getChannel().getIdLong(), "No Song playing");
            return;
        }

        musicManager.audioPlayer.stopTrack();
        musicManager.scheduler.clearQueue();
        message.sendNormalMessage(event.getChannel().getIdLong(), "Stopping Playlist");
    }

    @Override
    public String getHelp() {
        return "Clearing the Playlist";
    }
}
