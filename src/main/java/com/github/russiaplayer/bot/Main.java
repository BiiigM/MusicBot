package com.github.russiaplayer.bot;

import com.github.russiaplayer.SQL.ServerSQL;
import com.github.russiaplayer.commands.CommandRegistry;
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

        var sql = new ServerSQL();
        var registry = new CommandRegistry();


        jda.addEventListener(new Listener(registry, sql));
    }
}
