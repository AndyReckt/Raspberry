package me.andyreckt.raspberry.command;

import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import me.andyreckt.raspberry.data.CommandData;

public class RaspberryVelocityCommand extends RaspberryCommand {

    public RaspberryVelocityCommand() {
        super();
    }

    public RaspberryVelocityCommand(CommandData commandData) {
        super(commandData);
    }

    @Override
    protected Class<?> consoleClass() {
        return ConsoleCommandSource.class;
    }

    @Override
    protected Class<?> playerClass() {
        return Player.class;
    }
}
