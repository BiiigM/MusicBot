package com.github.russiaplayer.bot;

import com.github.russiaplayer.Entity.Server;
import com.github.russiaplayer.SQL.ServerRepo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

import static com.github.russiaplayer.bot.MessageSender.getMusicMessageData;

public class Setup {
    private final Guild guild;
    private Server server;
    private static final ServerRepo SERVER_REPO = ServerRepo.getInstance();
    private static final Logger LOG = LoggerFactory.getLogger(Setup.class);

    public Setup(Guild guild) {
        this.guild = guild;
        this.server = new Server();
        this.server.setGuildId(guild.getIdLong());
    }

    /**
     * Starts the setup and also handles all edge cases,
     * like if the text channel is there but the message not
     * and so on...
     */
    public void start() {
        SERVER_REPO.getByGuildID(guild.getIdLong()).ifPresent(value -> server = value);
        var textChannel = guild.getTextChannelById(server.getChannelId());

        if (textChannel == null) {
            createMusicChannel();
            return;
        }

        createMusicMessageIfPresent(textChannel);
    }

    private void createMusicChannel() {
        guild.createTextChannel("blyatradio").queue(textChannel -> {
            server.setChannelId(textChannel.getIdLong());
            createMusicMessageIfPresent(textChannel);
        });
    }

    private void createMusicMessageIfPresent(TextChannel textChannel) {
        textChannel.retrieveMessageById(server.getMusicMessageId()).queue(message ->
                        LOG.debug("Message found with ID: {} in TextChannel {} with ID {}", server.getMusicMessageId(),
                                textChannel.getName(), textChannel.getId()),
                throwable -> sendMusicMessage(textChannel, throwable));
    }

    private void sendMusicMessage(TextChannel textChannel, Throwable throwable) {
        if (throwable instanceof ErrorResponseException error &&
                error.getErrorResponse().equals(ErrorResponse.UNKNOWN_MESSAGE)) {
            textChannel.sendMessage(getMusicMessageData(new LinkedBlockingQueue<>(), null)).queue(message -> {
                server.setMusicMessageId(message.getIdLong());
                SERVER_REPO.save(server);
            });
        }
    }
}
