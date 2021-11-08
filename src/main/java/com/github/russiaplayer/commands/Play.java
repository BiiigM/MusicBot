package com.github.russiaplayer.commands;

import com.github.russiaplayer.music.PlayerManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Play implements Command{
    private final PlayerManager playerManager;

    public Play(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void action(GuildMessageReceivedEvent event) {
        event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
        playerManager.loadAndPlay(event.getChannel(), event.getMessage().getContentRaw());
    }

    @Override
    public String getHelp() {
        return "add a song to the Playlist. Can be a URL or a text. Playlists also work.";
    }
}
