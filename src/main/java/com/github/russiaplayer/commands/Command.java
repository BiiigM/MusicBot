package com.github.russiaplayer.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface Command {
    void action(SlashCommandInteractionEvent event);

    String getName();

    String getDescription();

    List<OptionData> getOptions();
}
