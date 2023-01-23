package com.github.russiaplayer.bot;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageSender {
    private static final Color DEFAULT_EMBED_COLOR = new Color(0xcc2576);
    private static final String DEFAULT_COVER = "https://cdn.pixabay.com/photo/2019/05/29/16/00/retro-4237850_960_720.jpg";

    public static MessageCreateData getMessageData(String message) {
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder.setEmbeds(new EmbedBuilder()
                .setDescription(message)
                .setColor(DEFAULT_EMBED_COLOR)
                .build());
        return messageCreateBuilder.build();
    }

    public static MessageCreateData getMusicMessageData(BlockingQueue<AudioTrack> queue) {
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder.setContent("__**Queue List:**__\n" + getQueueListMessage(queue));
        messageCreateBuilder.setEmbeds(getMusicEmbed(queue.poll()));
        messageCreateBuilder.setActionRow(Button.secondary("skip", Emoji.fromFormatted("⏭️")),
                Button.secondary("stop", Emoji.fromFormatted("⏹️")));
        return messageCreateBuilder.build();
    }

    private static String getQueueListMessage(BlockingQueue<AudioTrack> queue) {
        if (queue.isEmpty()) return "Join a VoiceChannel and add songs with the name or URL.";

        StringBuilder queueText = new StringBuilder();
        int trackCount = Math.min(queue.size(), 50);
        List<AudioTrack> trackList = new ArrayList<>(queue).subList(0, trackCount);

        if (queue.size() > 50) {
            queueText.append("and ")
                    .append(queue.size() - trackCount)
                    .append(" more songs.\n");
        }

        for (int i = trackCount - 1; i >= 0; i--) {
            var toAdd = (i + 1) + ". " + trackList.get(i).getInfo().title + "\n";
            if (toAdd.length() + queueText.length() + "__**Queue List:**__\n".length() > net.dv8tion.jda.api.entities.Message.MAX_CONTENT_LENGTH) {
                break;
            }
            queueText.append(toAdd);
        }
        return MarkdownSanitizer.sanitize(queueText.toString());
    }

    private static MessageEmbed getMusicEmbed(AudioTrack track) {
        if (track == null) {
            return new EmbedBuilder()
                    .setTitle("No Song is playing")
                    .setColor(DEFAULT_EMBED_COLOR)
                    .setImage(DEFAULT_COVER)
                    .build();
        }

        AudioTrackInfo trackInfo = track.getInfo();
        long trackLength = trackInfo.length;
        String title = trackInfo.title + " by " + trackInfo.author;
        URI ytUrl = URI.create(trackInfo.uri);
        String videoID = ytUrl.getQuery().substring(2);
        String imageUrl = "http://img.youtube.com/vi/" + videoID + "/0.jpg";

        return new EmbedBuilder()
                .setTitle(String.format("[%s] - %s", getTimeFromMilliseconds(trackLength), title))
                .setImage(imageUrl)
                .setColor(DEFAULT_EMBED_COLOR)
                .build();
    }

    private static String getTimeFromMilliseconds(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}
