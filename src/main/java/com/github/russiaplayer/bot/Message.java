package com.github.russiaplayer.bot;

import com.github.russiaplayer.SQL.ServerIDs;
import com.github.russiaplayer.SQL.ServerSQL;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import javax.sound.midi.Track;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Message {
    private final Guild guild;

    public Message(Guild guild){
        this.guild = guild;
    }

    public void sendNormalMessage(long channelID, String message){
        TextChannel channel = getChannel(channelID);
        channel.sendMessageEmbeds(new EmbedBuilder()
                .setDescription(message)
                .setColor(new Color(0xcc2576))
                .build())
                .delay(10, TimeUnit.SECONDS).flatMap(net.dv8tion.jda.api.entities.Message::delete).queue();
    }

    public void sendMusicMessage(TextChannel channel, ServerSQL sql){
        var queueText = "**Queue List:**\nJoin ein voice channel und queue songs mit dem Namen oder URL.";
        channel.sendMessage(queueText).setEmbeds(new EmbedBuilder()
                .setTitle("Kein Song am spielen")
                .setColor(new Color(0xcc2576))
                .setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSaajghqyNTOk3pWneCf8UKqGvo37xxB6zSjQ&usqp=CAU")
                .build()
        ).queue(message -> {
            sql.saveIds(new ServerIDs(channel.getGuild().getIdLong(), channel.getIdLong(), message.getIdLong()));
        });
    }

    public void updateMusicMessage(ServerSQL sql, BlockingQueue<AudioTrack> queue, AudioPlayer player){
        var iDs = sql.getIDs(guild.getIdLong());
        iDs.ifPresent(iDs1 -> {
            var channel = guild.getTextChannelById(iDs1.channel());
            if(channel == null) return;
            channel.editMessageById(iDs1.message(), new MessageBuilder()
                    .setContent("**Queue List:**" + getQueueListMessage(queue))
                    .setEmbeds(getMusicEmbed(player.getPlayingTrack())).build()).queue();

        });
    }

    private String getQueueListMessage(BlockingQueue<AudioTrack> queue){
        if(queue.isEmpty()) return "\nJoin ein voice channel und queue songs mit dem Namen oder URL.";

        String queueText = "";
        int trackCount = Math.min(queue.size(), 50);
        List<AudioTrack> trackList = new ArrayList<>(queue);

        if(trackCount >= 50) {
            queueText += "and " + (queue.size() - trackCount) + " more songs.\n";
        }

        for(int i = 0; i < trackCount; i++) {
            var toAdd = (i+1) + ". " + trackList.get(i).getInfo().title + "\n";
            if(toAdd.length() + queueText.length() + "**Queue List:**\n".length() > net.dv8tion.jda.api.entities.Message.MAX_CONTENT_LENGTH) {break;}
            queueText += toAdd;
        }
        return MarkdownSanitizer.sanitize(queueText);
    }

    private MessageEmbed getMusicEmbed(AudioTrack track){
        if(track == null) {
            return new EmbedBuilder()
                    .setTitle("Kein Song am spielen")
                    .setColor(new Color(0xcc2576))
                    .setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSaajghqyNTOk3pWneCf8UKqGvo37xxB6zSjQ&usqp=CAU")
                    .build();
        }
        var trackInfo = track.getInfo();
        var title = trackInfo.title;
        URI ytUrl = URI.create(trackInfo.uri);
        var videoID = ytUrl.getQuery().substring(2);
        var imageUrl = "http://img.youtube.com/vi/" + videoID + "/0.jpg";

        return new EmbedBuilder()
                .setTitle(title)
                .setImage(imageUrl)
                .setColor(new Color(0xcc2576)).build();
    }

    private TextChannel getChannel(long channelID){
        return guild.getTextChannelById(channelID);
    }
}
