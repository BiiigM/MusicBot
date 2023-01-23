package com.github.russiaplayer.commands;

import com.github.russiaplayer.bot.Setup;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

import static com.github.russiaplayer.bot.MessageSender.getMessageData;

public class SetupCommand implements Command {
    @Override
    public void action(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply(getMessageData("This bot only works on Server!!")).setEphemeral(true).queue();
            return;
        }

        Setup setup = new Setup(guild);
        setup.start();
        event.reply(getMessageData("Setup finished!")).setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public String getDescription() {
        return "Setup a channel and a music message. This could be ues if you deleted some thing.";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }
}
