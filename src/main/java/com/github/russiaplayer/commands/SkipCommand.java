package com.github.russiaplayer.commands;

import com.github.russiaplayer.exceptions.NotFoundException;
import com.github.russiaplayer.music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

import static com.github.russiaplayer.bot.MessageSender.getMessageData;

public class SkipCommand implements Command {
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

        PlayerManager.getInstance().getMusicManger(guild).scheduler.nextTrack();
        event.reply(getMessageData("Skipping current song.")).setEphemeral(true).queue();
    }

    public void action(ButtonInteractionEvent event) {
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

        PlayerManager.getInstance().getMusicManger(guild).scheduler.nextTrack();
        event.reply(getMessageData("Skipping current song.")).setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips the current song.";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }
}
