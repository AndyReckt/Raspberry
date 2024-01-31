package me.andyreckt.raspberry.data;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.andyreckt.raspberry.annotation.Children;
import me.andyreckt.raspberry.annotation.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class CommandData {
    private final String name;
    private final List<String> aliases;
    private final String description;
    private final String usage;
    private final String permission;
    private final String helpCommand;
    private final boolean autoHelp;
    private final boolean async;
    private final boolean hidden;
    private CommandData parent = null;

    public CommandData(Command command) {
        this.name = command.names()[0];
        this.aliases = Arrays.asList(command.names());
        this.description = command.description();
        this.usage = command.usage();
        this.permission = command.permission();
        this.helpCommand = command.helpCommand();
        this.autoHelp = command.autoHelp();
        this.async = command.async();
        this.hidden = command.hidden();
    }

    public CommandData(Children children, CommandData parent) {
        this.name = null;
        this.aliases = Arrays.asList(children.names());
        this.description = children.description();
        this.usage = children.usage();
        this.permission = children.permission();
        this.helpCommand = children.helpCommand();
        this.autoHelp = children.autoHelp();
        this.async = children.async();
        this.hidden = children.hidden();
        this.parent = parent;
    }
}
