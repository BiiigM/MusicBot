package com.github.russiaplayer.bot;

import com.github.russiaplayer.commands.CommandRegistry;
import com.github.russiaplayer.commands.PlayCommand;
import com.github.russiaplayer.commands.SetupCommand;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.ResourceBundle;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        ResourceBundle config = ResourceBundle.getBundle("config");

        var builder = JDABuilder.create(config.getString("token"), EnumSet.allOf(GatewayIntent.class));
        var jda = builder.build();
        CommandRegistry commandRegistry = new CommandRegistry(jda);

        try {
            jda.awaitReady();

            commandRegistry.registerCommand(new SetupCommand());
            commandRegistry.registerCommand(new PlayCommand());

            jda.addEventListener(new CommandEventListener(commandRegistry));
        } catch (Exception exp) {
            logger.error(exp.getMessage());
        }
    }
}
