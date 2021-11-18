package com.github.russiaplayer.bot;

import com.github.russiaplayer.music.PlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeaveTimer {

    private final JDA jda;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final List<Long> voiceChannels = new ArrayList<>();

    public LeaveTimer(JDA jda){
        this.jda = jda;
    }

    public void start(){
        executorService.scheduleAtFixedRate(this::run, 10, 10, TimeUnit.MINUTES);
    }

    private void run(){
        voiceChannels.stream()
                .map(jda::getVoiceChannelById)
                .filter(Objects::nonNull)
                .filter(voiceChannel -> voiceChannel.getMembers().size() == 1)
                .forEach(voiceChannel -> voiceChannel.getGuild().getAudioManager().closeAudioConnection());

        voiceChannels.clear();
        jda.getGuildCache().stream()
                .map(guild -> guild.getAudioManager().getConnectedChannel())
                .filter(Objects::nonNull)
                .filter(voiceChannel -> voiceChannel.getMembers().size() == 1)
                .map(GuildChannel::getIdLong)
                .forEach(voiceChannels::add);
    }
}
