package com.github.russiaplayer.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.github.russiaplayer.bot.MessageSender.updateMusicMessage;

public class TrackScheduler extends AudioEventAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackScheduler.class);
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    private final Guild guild;

    public TrackScheduler(@NotNull AudioPlayer player, @NotNull Guild guild) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.guild = guild;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        updateMusicMessage(guild, queue, track);
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            queue.offer(track);
        }
        updateMusicMessage(guild, queue, player.getPlayingTrack());
    }

    public void queue(AudioPlaylist tracks) {
        for (AudioTrack track : tracks.getTracks()) {
            if (!player.startTrack(track, true)) {
                queue.offer(track);
            }
        }
        updateMusicMessage(guild, queue, player.getPlayingTrack());
    }

    public void clearQueue() {
        queue.clear();
    }

    public void nextTrack() {
        AudioTrack nextTrack = queue.poll();
        this.player.startTrack(nextTrack, false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        } else if (endReason == AudioTrackEndReason.STOPPED) {
            updateMusicMessage(guild, queue, player.getPlayingTrack());
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        LOGGER.error("ERROR while trying to add track, trying again", exception);
        updateMusicMessage(guild, queue, player.getPlayingTrack());
        throw new RuntimeException(exception);
    }
}
