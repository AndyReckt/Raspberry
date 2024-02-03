package me.andyreckt.raspberry.example.ex3;

import me.andyreckt.raspberry.annotation.Children;
import me.andyreckt.raspberry.annotation.Command;
import me.andyreckt.raspberry.annotation.Flag;
import me.andyreckt.raspberry.annotation.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

@Command(names = "three")
public class CommandThree {

    @Children(names = {"one", "two"})
    public void childOne(CommandSender sender) {
        sender.sendMessage("Hello from child one & two!");
    }

    @Children(names = {"x", "y"})
    public void childTwo(ConsoleCommandSender sender, @Param(name = "z") String param0, @Flag(values = "f") boolean param1) {
        sender.sendMessage("Hello from child x & z!");
        sender.sendMessage("param0: " + param0);
        sender.sendMessage("param1: " + param1);
    }

    @Children(names = {"a", "b"})
    public void childThree(CommandSender sender, @Param(name = "c", wildcard = true, tabComplete = {"aze aze aze", "@players"}) String param0, @Flag(values = "f") boolean param1) {
        sender.sendMessage("Hello from child a & b!");
        sender.sendMessage("param0: " + param0);
        sender.sendMessage("param1: " + param1);
    }
}
