package com.github.russiaplayer.commands;

import com.github.russiaplayer.entity.Server;
import com.github.russiaplayer.exceptions.NotFoundException;
import com.github.russiaplayer.music.PlayerManager;
import com.github.russiaplayer.sql.ServerRepo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.russiaplayer.bot.MessageSender.getMessageData;
import static com.github.russiaplayer.bot.MessageSender.sendMessageToMusicChannel;
import static com.github.russiaplayer.utils.EventUtils.*;

public class PlayCommand implements Command {
    private static final ServerRepo SERVER_REPO = ServerRepo.getInstance();

    @Override
    public void action(SlashCommandInteractionEvent event) {
        try {
            Guild guild = getGuild(event);
            AudioChannelUnion userChannel = getUserChannel(event);
            AudioChannelUnion botChannel = getBotChannel(event);
            if (botChannel != userChannel) {
                event.reply(getMessageData("You are not in the same VoiceChannel")).setEphemeral(true).queue();
                return;
            }

            TextChannel musicChannel = getMusicChannel(guild);

            guild.getAudioManager().openAudioConnection(userChannel);
            String trackUrl = event.getOption("url").getAsString();
            if (!isUrl(trackUrl)) {
                trackUrl = "ytsearch:" + trackUrl;
            }

            PlayerManager.getInstance().loadAndPlay(musicChannel, trackUrl);
            event.reply(getMessageData("Now playing the song")).setEphemeral(true).queue();
        } catch (NotFoundException notFoundException) {
            event.reply(getMessageData(notFoundException.getFriendlyMessage())).setEphemeral(true).queue();
        }
    }

    public void action(MessageReceivedEvent event) {
        try {
            Guild guild = event.getGuild();
            AudioChannelUnion userChannel = getUserChannel(event);
            AudioChannelUnion botChannel = getBotChannel(event);
            if (botChannel != userChannel) {
                sendMessageToMusicChannel(guild, "You are not in the same VoiceChannel");
                return;
            }

            TextChannel musicChannel = getMusicChannel(guild);

            guild.getAudioManager().openAudioConnection(userChannel);
            String trackUrl = event.getMessage().getContentRaw();
            if (!isUrl(trackUrl)) {
                trackUrl = "ytsearch:" + trackUrl;
            }
            PlayerManager.getInstance().loadAndPlay(musicChannel, trackUrl);
        } catch (NotFoundException notFoundException) {
            event.getChannel().sendMessage(getMessageData(notFoundException.getFriendlyMessage())).queue();
        }
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

    @NotNull
    private static TextChannel getMusicChannel(Guild guild) throws NotFoundException {
        Optional<Server> server = SERVER_REPO.getByGuildID(guild.getIdLong());
        if (server.isEmpty()) {
            throw new NotFoundException("Guild with id: " + guild.getIdLong() + "Not found",
                    "We could not find your server in our List. Pls use the command /setup!");
        }

        TextChannel musicChannel = guild.getTextChannelById(server.get().getChannelId());
        if (musicChannel == null) {
            throw new NotFoundException("Music channel with id: " + server.get().getChannelId() + "Not found",
                    "We could not find your music channel. Pls use the command /setup");
        }
        return musicChannel;
    }
}
