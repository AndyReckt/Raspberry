package me.andyreckt.raspberry.velocity.completion;

import me.andyreckt.raspberry.command.VelocityCommandIssuer;
import me.andyreckt.raspberry.command.RaspberryVelocityCommand;
import me.andyreckt.raspberry.completions.CommandCompletionContext;

public class VelocityCommandCompletionContext extends CommandCompletionContext {
    public VelocityCommandCompletionContext(RaspberryVelocityCommand node, String argumentBeingCompleted, VelocityCommandIssuer issuer) {
        super(node, argumentBeingCompleted, issuer);
    }
}
