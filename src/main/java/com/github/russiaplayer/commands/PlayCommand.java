package com.github.russiaplayer.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class PlayCommand implements Command {
    @Override
    public void action(SlashCommandInteractionEvent event) {

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
        return null;
    }
}
