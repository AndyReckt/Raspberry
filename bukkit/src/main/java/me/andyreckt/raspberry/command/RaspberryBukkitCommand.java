package me.andyreckt.raspberry.command;

import me.andyreckt.raspberry.data.CommandData;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class RaspberryBukkitCommand extends RaspberryCommand {

    public RaspberryBukkitCommand() {
        super();
    }

    public RaspberryBukkitCommand(CommandData commandData) {
        super(commandData);
    }

    @Override
    protected Class<?> consoleClass() {
        return ConsoleCommandSender.class;
    }

    @Override
    protected Class<?> playerClass() {
        return Player.class;
    }
}
