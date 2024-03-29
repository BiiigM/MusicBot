package com.github.russiaplayer.bot;

import com.github.russiaplayer.commands.*;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);

        var builder = JDABuilder.create(System.getenv("BOT_TOKEN"), EnumSet.allOf(GatewayIntent.class));
        var jda = builder.build();
        CommandRegistry commandRegistry = new CommandRegistry(jda);

        try {
            jda.awaitReady();

            commandRegistry.registerCommand(new SetupCommand());
            commandRegistry.registerCommand(new PlayCommand());
            commandRegistry.registerCommand(new SkipCommand());
            commandRegistry.registerCommand(new StopCommand());
            commandRegistry.registerCommand(new LeaveCommand());

            jda.addEventListener(new CommandEventListener(commandRegistry));
        } catch (Exception exp) {
            logger.error(exp.getMessage());
        }
    }
}
