package me.andyreckt.raspberry.bukkit.completion;

import me.andyreckt.raspberry.command.BukkitCommandIssuer;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryBukkitCommand;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.completions.CommandCompletionContext;

public class BukkitCommandCompletionContext extends CommandCompletionContext {
    public BukkitCommandCompletionContext(RaspberryBukkitCommand node, String argumentBeingCompleted, BukkitCommandIssuer issuer) {
        super(node, argumentBeingCompleted, issuer);
    }
}
