package com.github.russiaplayer.bot;

import com.github.russiaplayer.Entity.Server;
import com.github.russiaplayer.SQL.ServerRepo;
import com.github.russiaplayer.exceptions.NotFoundException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    public static MessageCreateData getMusicMessageData(BlockingQueue<AudioTrack> queue, AudioTrack track) {
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder.setContent("__**Queue List:**__\n" + getQueueListMessage(queue));
        messageCreateBuilder.setEmbeds(getMusicEmbed(track));
        messageCreateBuilder.setActionRow(Button.secondary("skip", Emoji.fromFormatted("⏭️")),
                Button.secondary("stop", Emoji.fromFormatted("⏹️")));
        return messageCreateBuilder.build();
    }

    public static MessageEditData editMusicData(BlockingQueue<AudioTrack> queue, AudioTrack track) {
        MessageEditBuilder messageEditBuilder = new MessageEditBuilder();
        messageEditBuilder.setContent("__**Queue List:**__\n" + getQueueListMessage(queue));
        messageEditBuilder.setEmbeds(getMusicEmbed(track));
        messageEditBuilder.setActionRow(Button.secondary("skip", Emoji.fromFormatted("⏭️")),
                Button.secondary("stop", Emoji.fromFormatted("⏹️")));
        return messageEditBuilder.build();
    }

    public static void updateMusicMessage(Guild guild, BlockingQueue<AudioTrack> queue, AudioTrack track) throws NotFoundException {
        ServerRepo serverRepo = ServerRepo.getInstance();
        Optional<Server> server = serverRepo.getByGuildID(guild.getIdLong());
        if (server.isEmpty()) {
            throw new NotFoundException("Guild not found in server list.", "We can't find this server in our list. Pls do /setup.");
        }
        TextChannel textChannel = getMusicChannel(guild);
        textChannel.editMessageById(server.get().getMusicMessageId(), editMusicData(queue, track))
                .queue(message -> {
                }, throwable -> {
                    throw new NotFoundException("Music message not found.", "We can't find your music message. Pls do /setup.");
                });
    }

    public static void sendMessageToMusicChannel(Guild guild, String message) throws NotFoundException {
        getMusicChannel(guild).sendMessage(getMessageData(message))
                .delay(10, TimeUnit.SECONDS)
                .flatMap(net.dv8tion.jda.api.entities.Message::delete).queue();
    }

    private static TextChannel getMusicChannel(Guild guild) {
        ServerRepo serverRepo = ServerRepo.getInstance();
        Optional<Server> server = serverRepo.getByGuildID(guild.getIdLong());
        if (server.isEmpty()) {
            throw new NotFoundException("Guild not found in server list.", "We can't find this server in our list. Pls do /setup.");
        }
        TextChannel textChannel = guild.getTextChannelById(server.get().getChannelId());
        if (textChannel == null) {
            throw new NotFoundException("TextChannel not found.", "We can't find your music channel. Pls do /setup.");
        }
        return textChannel;
    }

    private static String getQueueListMessage(BlockingQueue<AudioTrack> queue) {
        if (queue.isEmpty()) return "Join a VoiceChannel and add songs with the name or URL.";

        int trackCount = Math.min(queue.size(), 50);
        int textLength = 0;
        List<AudioTrack> trackList = new ArrayList<>(queue).subList(0, trackCount);
        List<String> trackInfoList = new ArrayList<>();
        String overflowText = String.format("and %s more songs...\n", queue.size() - trackCount);

        for (int i = 0; i < trackCount; i++) {
            String toAdd = (i + 1) + ". " + trackList.get(i).getInfo().title + "\n";
            textLength += toAdd.length();
            if (textLength + overflowText.length()
                    + "__**Queue List:**__\n".length() > Message.MAX_CONTENT_LENGTH) {
                break;
            }
            trackInfoList.add(toAdd);
        }
        if (trackInfoList.size() < queue.size()) {
            trackInfoList.add(String.format("and %s more songs...\n", queue.size() - trackInfoList.size()));
        }
        Collections.reverse(trackInfoList);
        return MarkdownSanitizer.sanitize(String.join("", trackInfoList));
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
        String title = trackInfo.author + " - " + trackInfo.title;
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
