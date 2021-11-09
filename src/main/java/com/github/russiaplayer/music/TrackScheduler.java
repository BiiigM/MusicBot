package com.github.russiaplayer.music;

import com.github.russiaplayer.bot.Message;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final Message message;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        message = new Message(guild);
    }

    public void queue(AudioTrack track){
        if(!this.player.startTrack(track, true)){
            queue.offer(track);
            message.updateMusicMessage(queue, player);
            return;
        }
        message.updateMusicMessage(queue, player);
    }

    public void queue(AudioPlaylist tracks){
        for(AudioTrack track : tracks.getTracks()){
            if(!player.startTrack(track, true)){
                queue.offer(track);
            }
        }
        message.updateMusicMessage(queue, player);
    }

    public void clearQueue(){
        queue.clear();
        message.updateMusicMessage(queue, player);
    }

    public void nextTrack(){
        this.player.startTrack(queue.poll(), false);
        message.updateMusicMessage(queue, player);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext){
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        System.out.println(exception.getMessage());
    }
}
