package me.andyreckt.raspberry.example.ex2;

import me.andyreckt.raspberry.annotation.Command;
import me.andyreckt.raspberry.annotation.Flag;
import me.andyreckt.raspberry.annotation.Param;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTwo {

    @Command(names = "two")
    public void CommandTwo(Player sender) {
        sender.sendMessage("Hello from command two!");
    }

    @Command(names = "1")
    public void command1(CommandSender sender, @Flag(values = "f") boolean param0) {
        sender.sendMessage("Hello from command 1!");
        sender.sendMessage("param0: " + param0);
    }

    @Command(names = "2")
    public void command2(CommandSender sender, @Flag(values = "f") boolean param0, @Flag(values = "s", baseValue = true) boolean param1, @Param(name = "w", tabComplete = "@test") World param2) {
        sender.sendMessage("Hello from command 2!");
        sender.sendMessage("param0: " + param0);
        sender.sendMessage("param1: " + param1);
        sender.sendMessage("param2: " + param2.getName());
    }

}
