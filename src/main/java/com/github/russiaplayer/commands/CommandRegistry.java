package com.github.russiaplayer.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> mapper = new HashMap<>();
    public void registerCommand(String name, Command command){
        mapper.put(name, command);
    }
    public Command getCommand(String name){
        return mapper.get(name);
    }

    public Map<String, Command> getList(){
        return mapper;
    }
}
