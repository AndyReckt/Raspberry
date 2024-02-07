package me.andyreckt.raspberry.bukkit;

import me.andyreckt.raspberry.RaspberryBukkit;
import me.andyreckt.raspberry.arguments.Arguments;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryBukkitCommand;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.exception.InvalidArgumentException;
import me.andyreckt.raspberry.exception.InvalidExecutorException;
import me.andyreckt.raspberry.exception.MethodFailedException;
import me.andyreckt.raspberry.exception.UnknownCommandException;
import me.andyreckt.raspberry.message.IErrorMessageFormatter;
import me.andyreckt.raspberry.util.RaspberryBukkitUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class BukkitRaspberryCommand extends Command implements PluginIdentifiableCommand {
    private static final RaspberryBukkit raspberry = RaspberryBukkit.getBukkitInstance();
    private final Plugin plugin;
    private final RaspberryBukkitCommand command;

    public BukkitRaspberryCommand(RaspberryBukkitCommand command, Plugin plugin) {
        super(command.getName());
        this.command = command;
        this.plugin = plugin;

        this.setAliases(command.getRealAliases());
        this.setDescription(command.getDescription());
        this.setUsage(RaspberryBukkitUtils.color("&c" + command.getUsageText()));
        this.setPermission(command.getPermission());
        this.setPermissionMessage(raspberry.getCommandHandler().getNoPermissionMessage());
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if (command.isAsync()) raspberry.getExecutor().execute(() -> execute0(commandSender, args));
        else execute0(commandSender, args);

        return true;
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
            else sender.sendMessage(RaspberryBukkitUtils.color(raspberry.getCommandHandler().getUnknownCommandMessage()));
        } catch (InvalidExecutorException ex) {
            if (ex.consoleOnly) sender.sendMessage(RaspberryBukkitUtils.color(formatter.consoleOnly()));
            else sender.sendMessage(RaspberryBukkitUtils.color(formatter.playerOnly()));
        } catch (InvalidArgumentException ex) {
            sender.sendMessage(RaspberryBukkitUtils.color(formatter.invalidArgumentPrefix() + ex.getMessage()));
            if (ex.showSyntax()) sender.sendMessage(RaspberryBukkitUtils.color(formatter.usagePrefix() + executionNode.getUsageText()));
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(RaspberryBukkitUtils.color(formatter.internalError()));
            raspberry.getLogger().severe("An error occurred while attempting to perform command " + realLabel + " for " + sender.getName() + ":");
            ex.printStackTrace();
        } catch (MethodFailedException ex) {
            sender.sendMessage(RaspberryBukkitUtils.color(formatter.internalError()));
            raspberry.getLogger().severe("An error occurred while attempting to perform command " + realLabel + " for " + sender.getName() + ":");
            raspberry.getLogger().severe(ex.getMessage());
            ex.cause.printStackTrace();
        }
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (!(sender instanceof Player)) return new ArrayList<>();
        CommandIssuer<?> issuer = raspberry.getCommandIssuer(sender);
        return raspberry.getCommandHandler().getCompletions(command, issuer, args);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
