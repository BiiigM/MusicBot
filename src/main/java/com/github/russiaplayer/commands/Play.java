package com.github.russiaplayer.commands;

import com.github.russiaplayer.bot.Message;
import com.github.russiaplayer.music.PlayerManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.net.URI;
import java.net.URISyntaxException;

public class Play implements Command{
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

        event.getGuild().getAudioManager().openAudioConnection(userChannel);
        var content = event.getMessage().getContentRaw();
        var trackUrl = content.replace("play ", "");
        if(!isUrl(trackUrl)) {trackUrl = "ytsearch:" + trackUrl;}
        PlayerManager.getInstance().loadAndPlay(event.getChannel(), trackUrl);
    }

    private boolean isUrl(String url){
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    @Override
    public String getHelp() {
        return "add a song to the Playlist. Can be a URL or a text. Playlists also work.";
    }
}
