package me.andyreckt.raspberry.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import lombok.RequiredArgsConstructor;
import me.andyreckt.raspberry.RaspberryVelocity;
import me.andyreckt.raspberry.arguments.Arguments;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.exception.*;
import me.andyreckt.raspberry.message.IErrorMessageFormatter;
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

        if (executionNode.isAsync()) raspberry.getExecutor().execute(() -> execute1(sender, executionNode, arguments));
        else execute1(sender, executionNode, arguments);
    }

    private void execute1(CommandSource sender, RaspberryCommand executionNode, Arguments arguments) {
        String realLabel = executionNode.getFullLabel();
        IErrorMessageFormatter formatter = raspberry.getMessageFormatter().getErrorMessageFormatter();

        try {
            CommandIssuer<?> issuer = raspberry.getCommandIssuer(sender);

            executionNode.invoke(issuer, arguments);
        } catch (UnknownCommandException ex) {
            if (ex.showSyntax()) executionNode.sendHelp(raspberry.getCommandIssuer(sender));
            else sender.sendMessage(RaspberryVelocityUtils.color(raspberry.getCommandHandler().getUnknownCommandMessage()));
        } catch (InvalidExecutorException ex) {
            if (ex.consoleOnly) sender.sendMessage(RaspberryVelocityUtils.color(formatter.consoleOnly()));
            else sender.sendMessage(RaspberryVelocityUtils.color(formatter.playerOnly()));
        } catch (ConditionFailedException ex) {
            sender.sendMessage(RaspberryVelocityUtils.color(formatter.conditionFailedPrefix() + ex.getMessage()));
            if (ex.showSyntax()) executionNode.sendHelp(raspberry.getCommandIssuer(sender));
        } catch (InvalidArgumentException ex) {
            sender.sendMessage(RaspberryVelocityUtils.color(formatter.invalidArgumentPrefix() + ex.getMessage()));
            if (ex.showSyntax()) sender.sendMessage(RaspberryVelocityUtils.color(formatter.usagePrefix() + executionNode.getUsageText()));
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(RaspberryVelocityUtils.color(formatter.internalError()));
            raspberry.getLogger().severe("An error occurred while attempting to perform command " + realLabel + ":");
            ex.printStackTrace();
        } catch (MethodFailedException ex) {
            sender.sendMessage(RaspberryVelocityUtils.color(formatter.internalError()));
            raspberry.getLogger().severe("An error occurred while attempting to perform command " + realLabel + ":");
            raspberry.getLogger().severe(ex.getMessage());
            ex.cause.printStackTrace();
        }
    }
}

