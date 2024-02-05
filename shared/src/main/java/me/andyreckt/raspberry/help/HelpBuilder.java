package me.andyreckt.raspberry.help;

import lombok.Getter;
import me.andyreckt.raspberry.Raspberry;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.message.IHelpMessageFormatter;
import me.andyreckt.raspberry.util.ClickablePart;

import java.util.*;

@Getter
public class HelpBuilder {
    private final RaspberryCommand command;
    private final Set<HelpLine> lines = new HashSet<>();
    private final CommandIssuer<?> issuer;
    private int currentPage = 1;

    public HelpBuilder(CommandIssuer<?> issuer, RaspberryCommand command) {
        this.command = command;
        this.issuer = issuer;

        this.addLines(command.getChildren());
    }

    public HelpBuilder(CommandIssuer<?> issuer, RaspberryCommand command, int currentPage) {
        this.command = command;
        this.issuer = issuer;
        this.currentPage = Math.min(Math.max(1, currentPage), this.getTotalPages());

        this.addLines(command.getChildren());
    }

    public void addLines(Map<String, RaspberryCommand> children) {
        List<RaspberryCommand> commands = new ArrayList<>(children.values());
        commands.sort((a, b) -> {
            String aName = a.getName();
            String bName = b.getName();

            if (aName == null) {
                aName = a.getAliases().get(0);
            }

            if (bName == null) {
                bName = b.getAliases().get(0);
            }

            return aName.compareTo(bName);
        });

        for (RaspberryCommand command : commands) {
            if (command.isHidden()) {
                continue;
            }

            if (!command.canUse(issuer)) {
                continue;
            }

            lines.add(new HelpLine(command));
        }
    }

    public String getPreviousPage() {
        return "/" + command.getFullLabel() + " help " + (currentPage - 1);
    }

    public String getNextPage() {
        return "/" + command.getFullLabel() + " help " + (currentPage + 1);
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) this.getResultsAmount() / 10);
    }

    public int getResultsAmount() {
        return this.lines.size();
    }

    public List<List<ClickablePart>> build() {
        IHelpMessageFormatter formatter = Raspberry.getInstance().getMessageFormatter().getHelpMessageFormatter();

        List<List<ClickablePart>> parts = new ArrayList<>();
        parts.add(Collections.singletonList(new ClickablePart(" ")));
        parts.add(Collections.singletonList(new ClickablePart(formatter.getHeaderFormat(this))));
        parts.add(Collections.singletonList(new ClickablePart(" ")));

        for (HelpLine line : new ArrayList<>(lines).subList((currentPage - 1) * 10, Math.min(lines.size(), currentPage * 10))) {
            if (line.getDescription() != null && !line.getDescription().isEmpty()) {
                parts.add(Collections.singletonList(new ClickablePart(
                        formatter.getCommandFormatWithDescription(line), "/" + line.getParent() + " " + line.getCommand()
                )));
            } else {
                parts.add(Collections.singletonList(new ClickablePart(
                        formatter.getCommandFormat(line), "/" + line.getParent() + " " + line.getCommand()
                )));
            }
        }

        parts.add(formatter.getFooterFormat(this));
        return parts;
    }
}
