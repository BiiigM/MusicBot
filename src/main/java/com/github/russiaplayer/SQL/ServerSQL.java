package com.github.russiaplayer.SQL;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ServerSQL {
    private final DataSource serverDB;

    public ServerSQL(){
        serverDB = createConfig();
        createTable();
    }

    private static DataSource createConfig(){
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:data/server.db");
        config.setMaximumPoolSize(1);
        return new HikariDataSource(config);
    }

    private void createTable(){
        try(var connection = serverDB.getConnection();
            var statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS data (ServerID BIGINT,ChannelID BIGINT,MessageID BIGINT,PRIMARY KEY (ServerID));")){
            statement.execute();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }

    public void saveIds(ServerIDs iDs){
        try(var connection = serverDB.getConnection();
            var statement = connection.prepareStatement("INSERT INTO data(ServerID, ChannelID, MessageID) VALUES(?, ?, ?)" +
                    " ON CONFLICT do update set ChannelID = ?, MessageID = ?;"))
        {
            statement.setLong(1, iDs.server());
            statement.setLong(2, iDs.channel());
            statement.setLong(3, iDs.message());
            statement.setLong(4, iDs.channel());
            statement.setLong(5, iDs.message());
            statement.execute();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }

    public Optional<ServerIDs> getIDs(long serverID){
        try(var connection = serverDB.getConnection();
            var statement = connection.prepareStatement("SELECT * FROM data WHERE ServerID = ?")){
            statement.setLong(1, serverID);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                long channelID = result.getLong(2);
                long messageID = result.getLong(3);

                return Optional.of(new ServerIDs(serverID, channelID, messageID));
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return Optional.empty();
    }
}
