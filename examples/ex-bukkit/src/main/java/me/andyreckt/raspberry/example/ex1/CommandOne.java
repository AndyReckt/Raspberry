package me.andyreckt.raspberry.example.ex1;

import me.andyreckt.raspberry.annotation.Command;
import me.andyreckt.raspberry.annotation.Param;
import org.bukkit.command.CommandSender;

public class CommandOne {

    @Command(names = "one")
    public static void command(CommandSender sender) {
        sender.sendMessage("Hello from command one!");
    }

    @Command(names = "0")
    public static void command0(CommandSender sender, @Param(name = "number") int param0) {
        sender.sendMessage("Hello from command 0!");
        sender.sendMessage("param0: " + param0);
    }

}
