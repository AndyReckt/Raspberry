package me.andyreckt.raspberry.completions;

import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryCommand;

public abstract class CommandCompletionContext {
    private final RaspberryCommand command;
    private final String input;
    private final CommandIssuer<?> issuer;

    public CommandCompletionContext(RaspberryCommand command, String input, CommandIssuer<?> issuer) {
        this.command = command;
        this.input = input;
        this.issuer = issuer;
    }
}
