package com.github.russiaplayer.utils;

import com.github.russiaplayer.exceptions.NotFoundException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class EventUtils {
    @NotNull
    public static Guild getGuild(GenericInteractionCreateEvent event) throws NotFoundException {
        Guild guild = event.getGuild();
        if (guild == null) {
            throw new NotFoundException("Guild not found!", "You can only use it on a server.");
        }
        return guild;
    }

    @NotNull
    public static AudioChannelUnion getUserChannel(GenericInteractionCreateEvent event) throws NotFoundException {
        Member member = event.getMember();

        if (member == null) {
            throw new NotFoundException("no member found", "You are not a Member!?");
        }

        GuildVoiceState guildVoiceState = getVoiceState(member);

        AudioChannelUnion userChannel = guildVoiceState.getChannel();

        if (userChannel == null) {
            throw new NotFoundException("No channel for user " + member.getUser().getName() + " found", "You must join a VoiceChannel!");
        }

        return userChannel;
    }

    @NotNull
    public static AudioChannelUnion getBotChannel(GenericInteractionCreateEvent event) throws NotFoundException {
        Guild guild = getGuild(event);

        GuildVoiceState guildVoiceState = getVoiceState(guild.getSelfMember());

        AudioChannelUnion userChannel = guildVoiceState.getChannel();

        if (userChannel == null) {
            throw new NotFoundException("No channel for user found", "You must join a VoiceChannel!");
        }

        return userChannel;
    }

    @NotNull
    public static AudioChannelUnion getUserChannel(MessageReceivedEvent event) throws NotFoundException {
        Member member = event.getMember();

        if (member == null) {
            throw new NotFoundException("no member found", "You are not a Member!?");
        }

        GuildVoiceState guildVoiceState = getVoiceState(member);

        AudioChannelUnion userChannel = guildVoiceState.getChannel();

        if (userChannel == null) {
            throw new NotFoundException("No channel for user " + member.getUser().getName() + " found", "You must join a VoiceChannel!");
        }

        return userChannel;
    }

    @NotNull
    public static AudioChannelUnion getBotChannel(GenericMessageEvent event) throws NotFoundException {
        Guild guild = event.getGuild();

        GuildVoiceState guildVoiceState = getVoiceState(guild.getSelfMember());

        AudioChannelUnion userChannel = guildVoiceState.getChannel();

        if (userChannel == null) {
            throw new NotFoundException("No channel for user found", "You must join a VoiceChannel!");
        }

        return userChannel;
    }

    @NotNull
    public static GuildVoiceState getVoiceState(Member member) throws NotFoundException {
        GuildVoiceState guildVoiceState = member.getVoiceState();
        if (guildVoiceState == null) {
            throw new NotFoundException("No VoiceState found", "We didn't found a voice state for the bot?!");
        }
        return guildVoiceState;
    }
}
