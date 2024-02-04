package me.andyreckt.raspberry.bungee;

import me.andyreckt.raspberry.RaspberryBungee;
import me.andyreckt.raspberry.arguments.Arguments;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.exception.InvalidArgumentException;
import me.andyreckt.raspberry.exception.InvalidExecutorException;
import me.andyreckt.raspberry.exception.MethodFailedException;
import me.andyreckt.raspberry.exception.UnknownCommandException;
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
        String realLabel = executionNode.getFullLabel();

        try {
            CommandIssuer<?> issuer = raspberry.getCommandIssuer(sender);

            executionNode.invoke(issuer, arguments);
        } catch (UnknownCommandException ex) {
            if (ex.showSyntax()) sender.sendMessage(RaspberryBungeeUtils.color(executionNode.sendHelp(arguments)));
            else sender.sendMessage(RaspberryBungeeUtils.color(raspberry.getCommandHandler().getUnknownCommandMessage()));
        } catch (InvalidExecutorException ex) {
            if (ex.consoleOnly) sender.sendMessage(RaspberryBungeeUtils.color("&cThis command can only be executed by the console."));
            else sender.sendMessage(RaspberryBungeeUtils.color("&cThis command can only be executed by a player."));
        } catch (InvalidArgumentException ex) {
            sender.sendMessage(RaspberryBungeeUtils.color("&c" + ex.getMessage()));
            if (ex.showSyntax()) sender.sendMessage(RaspberryBungeeUtils.color("&cUsage: &7" + executionNode.getUsage()));
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(RaspberryBungeeUtils.color("&cAn internal error occurred while attempting to perform this command."));
            raspberry.getLogger().severe("An error occurred while attempting to perform command " + realLabel + " for " + sender.getName() + ":");
            ex.printStackTrace();
        } catch (MethodFailedException ex) {
            sender.sendMessage(RaspberryBungeeUtils.color("&cAn internal error occurred while attempting to perform this command."));
            raspberry.getLogger().severe("An error occurred while attempting to perform command " + realLabel + " for " + sender.getName() + ":");
            raspberry.getLogger().severe(ex.getMessage());
            ex.cause.printStackTrace();
        }
    }
}
