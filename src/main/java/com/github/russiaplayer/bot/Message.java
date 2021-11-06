package com.github.russiaplayer.bot;

import com.github.russiaplayer.SQL.ServerIDs;
import com.github.russiaplayer.SQL.ServerSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Message {
    private Guild guild;

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

    private TextChannel getChannel(long channelID){
        return guild.getTextChannelById(channelID);
    }
}
