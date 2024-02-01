package me.andyreckt.raspberry.bukkit;

import me.andyreckt.raspberry.RaspberryBukkit;
import me.andyreckt.raspberry.adapter.ParameterTypeAdapter;
import me.andyreckt.raspberry.arguments.Arguments;
import me.andyreckt.raspberry.bukkit.completion.BukkitCommandCompletionContext;
import me.andyreckt.raspberry.command.BukkitCommandIssuer;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryBukkitCommand;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.completions.CommandCompletionAction;
import me.andyreckt.raspberry.data.FlagData;
import me.andyreckt.raspberry.data.ParameterData;
import me.andyreckt.raspberry.exception.InvalidArgumentException;
import me.andyreckt.raspberry.exception.InvalidExecutorException;
import me.andyreckt.raspberry.exception.MethodFailedException;
import me.andyreckt.raspberry.exception.UnknownCommandException;
import me.andyreckt.raspberry.util.RaspberryBukkitUtils;
import me.andyreckt.raspberry.util.RaspberryConstant;
import me.andyreckt.raspberry.util.RaspberryUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

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
        this.setUsage(command.getUsage());
        this.setPermission(command.getPermission());
        this.setPermissionMessage(raspberry.getCommandHandler().getNoPermissionMessage());
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if (command.isAsync()) {
            raspberry.getExecutor().execute(() -> execute0(commandSender, args));
        } else {
            execute0(commandSender, args);
        }

        return true;
    }

    private void execute0(CommandSender sender, String[] args) {
        Arguments arguments = raspberry.getCommandHandler().processArguments(args);

        RaspberryCommand executionNode = command.findCommand(arguments);
        String realLabel = executionNode.getFullLabel();

        try {
            CommandIssuer<?> issuer = raspberry.getCommandIssuer(sender);

            executionNode.invoke(issuer, arguments);
        } catch (UnknownCommandException ex) {
            if (ex.showSyntax()) sender.sendMessage(RaspberryBukkitUtils.color(executionNode.sendHelp(arguments)));
            else sender.sendMessage(RaspberryBukkitUtils.color(raspberry.getCommandHandler().getUnknownCommandMessage()));
        } catch (InvalidExecutorException ex) {
            if (ex.consoleOnly) sender.sendMessage(RaspberryBukkitUtils.color("&cThis command can only be executed by the console."));
            else sender.sendMessage(RaspberryBukkitUtils.color("&cThis command can only be executed by a player."));
        } catch (InvalidArgumentException ex) {
            sender.sendMessage(RaspberryBukkitUtils.color("&c" + ex.getMessage()));
            sender.sendMessage(RaspberryBukkitUtils.color(executionNode.getUsage()));
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(RaspberryBukkitUtils.color("&cAn internal error occurred while attempting to perform this command."));
            raspberry.getLogger().severe("An error occurred while attempting to perform command " + realLabel + " for " + sender.getName() + ":");
            ex.printStackTrace();
        } catch (MethodFailedException ex) {
            sender.sendMessage(RaspberryBukkitUtils.color("&cAn internal error occurred while attempting to perform this command."));
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
