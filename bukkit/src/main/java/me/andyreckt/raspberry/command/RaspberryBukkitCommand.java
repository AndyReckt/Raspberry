package me.andyreckt.raspberry.command;

import me.andyreckt.raspberry.data.CommandData;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class RaspberryBukkitCommand extends RaspberryCommand {

    public RaspberryBukkitCommand() {
        super();
    }

    public RaspberryBukkitCommand(Class<?> owningClass, Object instance) {
        super(owningClass, instance);
    }

    public RaspberryBukkitCommand(String name, String description, String permission) {
        super(name, description, permission);
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
