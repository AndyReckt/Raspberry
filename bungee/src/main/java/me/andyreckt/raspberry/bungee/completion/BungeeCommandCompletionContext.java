package me.andyreckt.raspberry.bungee.completion;

import me.andyreckt.raspberry.command.BungeeCommandIssuer;
import me.andyreckt.raspberry.command.RaspberryBungeeCommand;
import me.andyreckt.raspberry.completions.CommandCompletionContext;

public class BungeeCommandCompletionContext extends CommandCompletionContext {
    public BungeeCommandCompletionContext(RaspberryBungeeCommand node, String argumentBeingCompleted, BungeeCommandIssuer issuer) {
        super(node, argumentBeingCompleted, issuer);
    }
}
