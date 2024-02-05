package me.andyreckt.raspberry.help;

import me.andyreckt.raspberry.command.RaspberryCommand;

public class HelpLine {

    private final RaspberryCommand command;

    public HelpLine(RaspberryCommand command) {
        this.command = command;
    }

    public String getUsage() {
        return command.getParametersUsage();
    }

    public String getDescription() {
        return command.getDescription();
    }

    public String getParent() {
        return command.getParent().getName();
    }

    public String getCommand() {
        return command.getName() != null ? command.getName() : command.getAliases().get(0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        HelpLine helpLine = (HelpLine) obj;

        return command.getName().equals(helpLine.command.getName());
    }

    @Override
    public int hashCode() {
        return command.getName().hashCode();
    }
}
