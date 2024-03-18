package me.andyreckt.raspberry.bungee;

import me.andyreckt.raspberry.RaspberryBungee;
import me.andyreckt.raspberry.arguments.Arguments;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.exception.*;
import me.andyreckt.raspberry.message.IErrorMessageFormatter;
import me.andyreckt.raspberry.util.RaspberryBungeeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;

public class BungeeRaspberryCommand extends Command implements TabExecutor {
    private static final RaspberryBungee raspberry = RaspberryBungee.getBungeeInstance();

    private final RaspberryCommand command;

    public BungeeRaspberryCommand(RaspberryCommand command) {
        super(command.getName(), command.getPermission(), command.getAliases().toArray(new String[0]));
        this.command = command;

        this.setPermissionMessage(raspberry.getCommandHandler().getNoPermissionMessage());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (command.isAsync()) raspberry.getExecutor().execute(() -> execute0(sender, args));
        else execute0(sender, args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return new ArrayList<>();
        CommandIssuer<?> issuer = raspberry.getCommandIssuer(sender);
        return raspberry.getCommandHandler().getCompletions(command, issuer, args);
    }

    private void execute0(CommandSender sender, String[] args) {
        Arguments arguments = raspberry.getCommandHandler().processArguments(args);

        RaspberryCommand executionNode = command.findCommand(arguments);

        if (executionNode == null) {
            int page = 1;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {
                }
            }
            raspberry.getCommandHandler().sendHelp(command, raspberry.getCommandIssuer(sender), page);
            return;
        }

        if (executionNode.isAsync()) raspberry.getExecutor().execute(() -> execute1(executionNode, sender, arguments));
        else execute1(executionNode, sender, arguments);
    }

    private void execute1(RaspberryCommand executionNode, CommandSender sender, Arguments arguments) {
        String realLabel = executionNode.getFullLabel();
        IErrorMessageFormatter formatter = raspberry.getMessageFormatter().getErrorMessageFormatter();

        try {
            CommandIssuer<?> issuer = raspberry.getCommandIssuer(sender);

            executionNode.invoke(issuer, arguments);
        } catch (UnknownCommandException ex) {
            if (ex.showSyntax()) executionNode.sendHelp(raspberry.getCommandIssuer(sender));
            else
                sender.sendMessage(RaspberryBungeeUtils.color(raspberry.getCommandHandler().getUnknownCommandMessage()));
        } catch (InvalidExecutorException ex) {
            if (ex.consoleOnly) sender.sendMessage(RaspberryBungeeUtils.color(formatter.consoleOnly()));
            else sender.sendMessage(RaspberryBungeeUtils.color(formatter.playerOnly()));
        } catch (ConditionFailedException ex) {
            sender.sendMessage(RaspberryBungeeUtils.color(formatter.conditionFailedPrefix() + ex.getMessage()));
            if (ex.showSyntax()) executionNode.sendHelp(raspberry.getCommandIssuer(sender));
        } catch (InvalidArgumentException ex) {
            sender.sendMessage(RaspberryBungeeUtils.color(formatter.invalidArgumentPrefix() + ex.getMessage()));
            if (ex.showSyntax())
                sender.sendMessage(RaspberryBungeeUtils.color(formatter.usagePrefix() + executionNode.getUsageText()));
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(RaspberryBungeeUtils.color(formatter.internalError()));
            raspberry.getLogger().severe("An error occurred while attempting to perform command " + realLabel + " for " + sender.getName() + ":");
            ex.printStackTrace();
        } catch (MethodFailedException ex) {
            sender.sendMessage(RaspberryBungeeUtils.color(formatter.internalError()));
            raspberry.getLogger().severe("An error occurred while attempting to perform command " + realLabel + " for " + sender.getName() + ":");
            raspberry.getLogger().severe(ex.getMessage());
            ex.cause.printStackTrace();
        }
    }
}
