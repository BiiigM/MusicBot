package com.github.russiaplayer.music;

import com.github.russiaplayer.bot.Message;
import com.github.russiaplayer.commands.Play;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager instance;
    private final Map<Long, GuildMusicManager> musicManager;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManager = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManger(Guild guild) {
        return this.musicManager.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, guild);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, String trackUrl){
        final GuildMusicManager musicManager = getMusicManger(channel.getGuild());
        Message message = new Message(channel.getGuild());

        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);
                message.sendNormalMessage(channel.getIdLong(), "Adding: "
                        + MarkdownSanitizer.sanitize(audioTrack.getInfo().title)
                        + " by "
                        + MarkdownSanitizer.sanitize(audioTrack.getInfo().author));
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if(trackUrl.startsWith("ytsearch:")){
                    var audioTrack = audioPlaylist.getTracks().get(0);
                    musicManager.scheduler.queue(audioTrack);
                    message.sendNormalMessage(channel.getIdLong(), "Adding: "
                            + MarkdownSanitizer.sanitize(audioTrack.getInfo().title)
                            + " by "
                            + MarkdownSanitizer.sanitize(audioTrack.getInfo().author));
                    return;
                }
                musicManager.scheduler.queue(audioPlaylist);
                message.sendNormalMessage(channel.getIdLong(), "Adding: " + audioPlaylist.getTracks().size() + " to Playlist");
            }

            @Override
            public void noMatches() {
                message.sendNormalMessage(channel.getIdLong(), "No Match found");
            }

            @Override
            public void loadFailed(FriendlyException e) {
                System.out.println(e.toString());
                message.sendNormalMessage(channel.getIdLong(), "ERROR: " + e);
            }
        });
    }

    public static PlayerManager getInstance(){
        if(instance == null){
            instance = new PlayerManager();
        }
        return instance;
    }
}
