package com.github.russiaplayer.commands;

import com.github.russiaplayer.entity.Server;
import com.github.russiaplayer.exceptions.NotFoundException;
import com.github.russiaplayer.music.PlayerManager;
import com.github.russiaplayer.sql.ServerRepo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.russiaplayer.bot.MessageSender.getMessageData;
import static com.github.russiaplayer.bot.MessageSender.sendMessageToMusicChannel;

public class PlayCommand implements Command {
    private static final ServerRepo SERVER_REPO = ServerRepo.getInstance();

    @Override
    public void action(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();

        GuildVoiceState userVoiceState = event.getMember().getVoiceState();
        GuildVoiceState botVoiceState = guild.getSelfMember().getVoiceState();

        if (userVoiceState == null || botVoiceState == null) {
            throw new NotFoundException("VoiceState not found", "Your or the VoiceState of the bot was not found. Pls contact us!");
        }

        AudioChannelUnion userChannel = event.getMember().getVoiceState().getChannel();
        AudioChannelUnion botChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();

        if (userChannel == null) {
            event.reply(getMessageData("You must join a VoiceChannel")).setEphemeral(true).queue();
            return;
        }

        if (botChannel != null && botChannel != userChannel) {
            event.reply(getMessageData("You are not in the same VoiceChannel")).setEphemeral(true).queue();
            return;
        }

        Optional<Server> server = SERVER_REPO.getByGuildID(guild.getIdLong());
        if (server.isEmpty()) {
            event.reply(getMessageData("We could not find your server in our List. Pls use the command /setup"))
                    .setEphemeral(true).queue();
            return;
        }

        TextChannel musicChannel = guild.getTextChannelById(server.get().getChannelId());
        if (musicChannel == null) {
            event.reply(getMessageData("We could not find your music channel. Pls use the command /setup"))
                    .setEphemeral(true).queue();
            return;
        }

        guild.getAudioManager().openAudioConnection(userChannel);
        String trackUrl = event.getOption("url").getAsString();
        if (!isUrl(trackUrl)) {
            trackUrl = "ytsearch:" + trackUrl;
        }
        PlayerManager.getInstance().loadAndPlay(musicChannel, trackUrl);
        event.reply(getMessageData("Now playing the song")).setEphemeral(true).queue();
    }

    public void action(MessageReceivedEvent event) {
        Guild guild = event.getGuild();

        GuildVoiceState userVoiceState = event.getMember().getVoiceState();
        GuildVoiceState botVoiceState = guild.getSelfMember().getVoiceState();

        if (userVoiceState == null || botVoiceState == null) {
            throw new NotFoundException("VoiceState not found", "Your or the VoiceState of the bot was not found. Pls contact us!");
        }

        AudioChannelUnion userChannel = event.getMember().getVoiceState().getChannel();
        AudioChannelUnion botChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();

        if (userChannel == null) {
            sendMessageToMusicChannel(guild, "You must join a VoiceChannel");
            return;
        }

        if (botChannel != null && botChannel != userChannel) {
            sendMessageToMusicChannel(guild, "You are not in the same VoiceChannel");
            return;
        }

        Optional<Server> server = SERVER_REPO.getByGuildID(guild.getIdLong());
        if (server.isEmpty()) {
            if (event.getChannelType() == ChannelType.TEXT) {
                event.getChannel().sendMessage(getMessageData("We could not find your server in our List. Pls use the command /setup")).queue();
            }
            return;
        }

        TextChannel musicChannel = guild.getTextChannelById(server.get().getChannelId());
        if (musicChannel == null) {
            if (event.getChannelType() == ChannelType.TEXT) {
                event.getChannel().sendMessage(getMessageData("We could not find your music channel. Pls use the command /setup")).queue();
            }
            return;
        }

        guild.getAudioManager().openAudioConnection(userChannel);
        String trackUrl = event.getMessage().getContentRaw();
        if (!isUrl(trackUrl)) {
            trackUrl = "ytsearch:" + trackUrl;
        }
        PlayerManager.getInstance().loadAndPlay(musicChannel, trackUrl);
    }

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Plays a song from YouTube, SoundCloud or Twitch streams.";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> optionDataList = new ArrayList<>();
        optionDataList.add(new OptionData(OptionType.STRING, "url",
                "Adds a song to the queue. You can use URLs or the song names.", true));
        return optionDataList;
    }
}
