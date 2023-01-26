package com.github.russiaplayer.SQL;

import com.github.russiaplayer.Entity.Server;
import com.github.russiaplayer.exceptions.NotFoundException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ServerRepo {
    private static ServerRepo instance;
    private static final Logger LOG = LoggerFactory.getLogger(ServerRepo.class);
    private static final String PROPS_FILE = "config";
    private final DataSource dataSource;

    public ServerRepo() {
        ResourceBundle bundle = ResourceBundle.getBundle(PROPS_FILE);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(bundle.getString("jdbc"));
        config.setUsername(bundle.getString("user"));
        config.setPassword(bundle.getString("password"));
        config.setMaximumPoolSize(1);
        this.dataSource = new HikariDataSource(config);
    }

    public void save(Server server) {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("INSERT INTO server(guild_id, channel_id, music_message_id, news_message_id) " +
                     "VALUES(?, ?, ?, ?)" +
                     "ON DUPLICATE KEY UPDATE " +
                     "channel_id=?, music_message_id=?, news_message_id=?")) {
            statement.setLong(1, server.getGuildId());
            statement.setLong(2, server.getChannelId());
            statement.setLong(3, server.getMusicMessageId());
            statement.setLong(4, server.getNewsMessageId());
            statement.setLong(5, server.getChannelId());
            statement.setLong(6, server.getMusicMessageId());
            statement.setLong(7, server.getNewsMessageId());
            statement.execute();
            LOG.info("Added/Updated Server {} to database", server.getGuildId());
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    public void delete(Server server) {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("DELETE FROM server WHERE guild_id=?")) {
            statement.setLong(1, server.getGuildId());
            statement.execute();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }

    public Optional<List<Server>> getAll() {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("SELECT * FROM server")) {
            ResultSet resultSet = statement.executeQuery();
            List<Server> serverList = new ArrayList<>();
            while (resultSet.next()) {
                serverList.add(new Server(resultSet.getLong(1),
                        resultSet.getLong(2),
                        resultSet.getLong(3),
                        resultSet.getLong(4)));
            }
            return Optional.of(serverList);
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Server> getByGuildID(Long guildId) {
        return getAll().flatMap(servers -> servers.stream().filter(server -> guildId.equals(server.getGuildId())).findFirst());
    }

    public Server getByGuild(Guild guild) throws NotFoundException {
        Optional<Server> server = getByGuildID(guild.getIdLong());
        if (server.isEmpty()) {
            throw new NotFoundException("Server not found in List. With ID: " + guild.getIdLong(),
                    "We could not find your server in our List. Pls use the command /setup");
        }
        return server.get();
    }

    public TextChannel getMusicChannelByGuild(Guild guild) throws NotFoundException {
        Server server = getByGuild(guild);
        TextChannel musicChannel = guild.getTextChannelById(server.getChannelId());
        if (musicChannel == null) {
            throw new NotFoundException("Music Channel not found on guild: " + guild.getIdLong(),
                    "We could not find your music channel. Pls use the command /setup");
        }
        return musicChannel;
    }

    public static ServerRepo getInstance() {
        if (instance == null) {
            instance = new ServerRepo();
        }
        return instance;
    }
}
