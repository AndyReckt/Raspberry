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
    public boolean execute(CommandSender commandSender, String label0, String[] args) {
        String label = label0.replace(plugin.getName().toLowerCase() + ":", "");

        if (command.isAsync()) {
            raspberry.getExecutor().execute(() -> execute0(commandSender, label, args));
        } else {
            execute0(commandSender, label, args);
        }

        return true;
    }

    private void execute0(CommandSender sender, String label, String[] args) {
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
        Set<String> completions = new HashSet<>();
        if (!(sender instanceof Player)) return new ArrayList<>(completions);

        Arguments arguments = raspberry.getCommandHandler().processArguments(args);
        CommandIssuer<?> issuer = raspberry.getCommandIssuer(sender);

        RaspberryCommand node = command.findCommand(arguments);
        if (!node.canUse(issuer)) return new ArrayList<>(completions);

        List<String> realArgs = arguments.getArgs();

        int index = realArgs.size() - 1;
        if (index < 0) index = 0;
        if (args[args.length -1].equalsIgnoreCase(" ")) index++;

        if (node.hasChild()) {
            String name = realArgs.isEmpty() ? "" : realArgs.get(realArgs.size() - 1);
            completions.addAll(node.getChildren().values().stream()
                    .filter(it -> node.getName() != null && node.canUse(issuer) &&
                            (RaspberryUtils.startsWithIgnoreCase(node.getName(), name) || name == null || name.isEmpty()))
                    .map(RaspberryCommand::getName)
                    .collect(Collectors.toList()));


            if (!completions.isEmpty()) {
                return new ArrayList<>(completions);
            }
        }

        if (args[args.length - 1].equalsIgnoreCase(node.getName()) && !args[args.length -1].equalsIgnoreCase(" ")) {
            return new ArrayList<>(completions);
        }

        List<FlagData> possibleFlags = node.getParameters().stream()
                .filter(data -> data instanceof FlagData)
                .map(data -> (FlagData) data)
                .collect(Collectors.toList());

        if (!possibleFlags.isEmpty()) {
            for (FlagData flag : possibleFlags) {
                String arg = args[args.length - 1];
                if (RaspberryConstant.FLAG_PATTERN.matcher(arg).matches()
                        || arg.startsWith("-") && (RaspberryUtils.startsWithIgnoreCase(flag.values()[0], arg.substring(1)))
                        || arg.equals("-")
                        || arg.isEmpty() || arg.equals(" ")
                ) {
                    completions.add("-" + flag.values()[0]);
                }
            }
        }

        try {
            List<ParameterData> params = node.getParameters().stream()
                    .filter(param -> param instanceof ParameterData)
                    .map(param -> (ParameterData) param)
                    .collect(Collectors.toList());

            int fixed = Math.max(0, index - 1);

            if (params.isEmpty()) {
                return new ArrayList<>(completions);
            }

            ParameterData data = params.get(fixed);

            ParameterTypeAdapter<?> parameterType = raspberry.getCommandHandler().getTypeAdapter(data.clazz());

            if (parameterType != null) {
                if (index < realArgs.size() && args[index].equalsIgnoreCase(node.getName())) {
                    realArgs.add("");
                    ++index;
                }
                String argumentBeingCompleted = (index >= realArgs.size() || realArgs.isEmpty()) ? "" : realArgs.get(index).trim();

                String[] tabCompleteFlags = data.tabComplete();

                for (String flag : tabCompleteFlags) {
                    if (flag.startsWith("@")) {
                        CommandCompletionAction<?> action = raspberry.getCommandHandler().getCompletions().get(flag);

                        if (action != null) {
                            Collection<String> suggestions = action.get(new BukkitCommandCompletionContext((RaspberryBukkitCommand) node, argumentBeingCompleted, (BukkitCommandIssuer) issuer));
                            completions.addAll(suggestions.stream()
                                    .filter(s -> RaspberryUtils.startsWithIgnoreCase(s, argumentBeingCompleted))
                                    .collect(Collectors.toList()));
                        }
                        continue;
                    }
                    if (RaspberryUtils.startsWithIgnoreCase(flag, argumentBeingCompleted)) {
                        completions.add(flag);
                    }
                }

                List<String> suggested = parameterType.complete(issuer, argumentBeingCompleted);
                completions.addAll(suggested.stream()
                        .filter(s -> RaspberryUtils.startsWithIgnoreCase(s, argumentBeingCompleted))
                        .collect(Collectors.toList()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(completions);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
