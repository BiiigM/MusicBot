package com.github.russiaplayer.commands;

import com.github.russiaplayer.music.PlayerManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Play implements Command{
    @Override
    public void action(GuildMessageReceivedEvent event) {
    }

    @Override
    public String getHelp() {
        return "add a song to the Playlist. Can be a URL or a text. Playlists also work.";
    }
}
