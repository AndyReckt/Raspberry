package me.andyreckt.raspberry.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import lombok.RequiredArgsConstructor;
import me.andyreckt.raspberry.RaspberryVelocity;
import me.andyreckt.raspberry.arguments.Arguments;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.exception.InvalidArgumentException;
import me.andyreckt.raspberry.exception.InvalidExecutorException;
import me.andyreckt.raspberry.exception.MethodFailedException;
import me.andyreckt.raspberry.exception.UnknownCommandException;
import me.andyreckt.raspberry.util.RaspberryVelocityUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class VelocityRaspberryCommand implements SimpleCommand {
    private static final RaspberryVelocity raspberry = RaspberryVelocity.getVelocityInstance();

    private final RaspberryCommand command;

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (command.isAsync()) raspberry.getExecutor().execute(() -> execute0(sender, args));
        else execute0(sender, args);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        CommandIssuer<?> issuer = raspberry.getCommandIssuer(invocation.source());
        return raspberry.getCommandHandler().getCompletions(command, issuer, invocation.arguments());
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> suggest(invocation));
    }


    @Override
    public boolean hasPermission(Invocation invocation) {
        CommandIssuer<?> issuer = raspberry.getCommandIssuer(invocation.source());
        return command.getPermission() == null || command.getPermission().isEmpty() || issuer.hasPermission(command.getPermission());
    }

    private void execute0(CommandSource sender, String[] args) {
        Arguments arguments = raspberry.getCommandHandler().processArguments(args);

        RaspberryCommand executionNode = command.findCommand(arguments);
        String realLabel = executionNode.getFullLabel();

        try {
            CommandIssuer<?> issuer = raspberry.getCommandIssuer(sender);

            executionNode.invoke(issuer, arguments);
        } catch (UnknownCommandException ex) {
            if (ex.showSyntax()) sender.sendMessage(RaspberryVelocityUtils.color(executionNode.sendHelp(arguments)));
            else
                sender.sendMessage(RaspberryVelocityUtils.color(raspberry.getCommandHandler().getUnknownCommandMessage()));
        } catch (InvalidExecutorException ex) {
            if (ex.consoleOnly)
                sender.sendMessage(RaspberryVelocityUtils.color("&cThis command can only be executed by the console."));
            else sender.sendMessage(RaspberryVelocityUtils.color("&cThis command can only be executed by a player."));
        } catch (InvalidArgumentException ex) {
            sender.sendMessage(RaspberryVelocityUtils.color("&c" + ex.getMessage()));
            if (ex.showSyntax()) sender.sendMessage(RaspberryVelocityUtils.color("&cUsage: &7" + executionNode.getUsage()));
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(RaspberryVelocityUtils.color("&cAn internal error occurred while attempting to perform this command."));
            raspberry.getLogger().severe("An error occurred while attempting to perform command " + realLabel + ":");
            ex.printStackTrace();
        } catch (MethodFailedException ex) {
            sender.sendMessage(RaspberryVelocityUtils.color("&cAn internal error occurred while attempting to perform this command."));
            raspberry.getLogger().severe("An error occurred while attempting to perform command " + realLabel + ":");
            raspberry.getLogger().severe(ex.getMessage());
            ex.cause.printStackTrace();
        }
    }
}

