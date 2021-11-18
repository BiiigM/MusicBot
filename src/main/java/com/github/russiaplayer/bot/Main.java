package com.github.russiaplayer.bot;

import com.github.russiaplayer.commands.*;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.ResourceBundle;

public class Main {
    public static void main(String[] args) throws LoginException {
        ResourceBundle config = ResourceBundle.getBundle("config");
        var builder = JDABuilder.create(config.getString("token"), EnumSet.allOf(GatewayIntent.class));
        var jda = builder.build();

        var registry = new CommandRegistry();
        registry.registerCommand("setup", new Setup());
        registry.registerCommand("play", new Play());
        registry.registerCommand("stop", new Stop());
        registry.registerCommand("skip", new Skip());
        registry.registerCommand("leave", new Leave());

        jda.addEventListener(new Listener(registry));
        new LeaveTimer(jda).start();
    }
}
