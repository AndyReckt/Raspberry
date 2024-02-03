package me.andyreckt.raspberry.command;

import me.andyreckt.raspberry.data.CommandData;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;

public class RaspberryBungeeCommand extends RaspberryCommand {

    public RaspberryBungeeCommand() {
        super();
    }

    public RaspberryBungeeCommand(CommandData commandData) {
        super(commandData);
    }

    @Override
    protected Class<?> consoleClass() {
        return ConsoleCommandSender.class;
    }

    @Override
    protected Class<?> playerClass() {
        return ProxiedPlayer.class;
    }
}
