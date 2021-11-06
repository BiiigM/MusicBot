package com.github.russiaplayer.commands;

import com.github.russiaplayer.SQL.ServerIDs;
import com.github.russiaplayer.SQL.ServerSQL;
import com.github.russiaplayer.bot.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class Setup implements Command{
    ServerSQL sql;
    public Setup(ServerSQL sql){
        this.sql = sql;
    }

    @Override
    public void action(GuildMessageReceivedEvent event) {
        Message message = new Message(event.getGuild());
        sql.getIDs(event.getGuild().getIdLong()).ifPresentOrElse(iDs -> {
            //creating new MusicChannel if server was in db
            var channel = event.getGuild().getTextChannelById(iDs.channel());
            if(channel == null){
                createTextChannel(event);
                return;
            }

            //creating new Message if longer in channel
            channel.retrieveMessageById(iDs.message()).queue(message1 -> {
                message.sendNormalMessage(event.getChannel().getIdLong(), "You already have a MusicChannel");
            }, throwable -> {
                if(throwable instanceof ErrorResponseException error){
                    if(error.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE){
                        sendMusicMessage(channel);
                    }
                }
            });
        }, () -> createTextChannel(event));
    }

    private void createTextChannel(GuildMessageReceivedEvent event){
        event.getGuild().createTextChannel("RussianMusic").queue(this::sendMusicMessage);
    }

    private void sendMusicMessage(TextChannel channel){
        Message message = new Message(channel.getGuild());
        message.sendMusicMessage(channel, sql);
    }

    @Override
    public String getHelp() {
        return "create a MusicChannel for you Server.";
    }
}
