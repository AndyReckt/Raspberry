package me.andyreckt.raspberry.modern.impl;

import me.andyreckt.raspberry.RaspberryBukkit;
import me.andyreckt.raspberry.bukkit.BukkitRaspberryCommand;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.modern.ModernCommandMap;
import me.andyreckt.raspberry.util.RaspberryUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_20_R3.command.CraftCommandMap;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModernCommandMapV1_20_R3 implements ModernCommandMap {
    private final CommandMap commandMap = new CommandMap();

    static class CommandMap extends CraftCommandMap {
        public CommandMap() {
            super(Bukkit.getServer());
        }
        public List<String> tabComplete(CommandSender sender, String cmdLine) {
            if (sender == null) {
                throw new IllegalArgumentException("Sender cannot be null");
            }
            if (cmdLine == null) {
                throw new IllegalArgumentException("Command line cannot be null");
            }
            int spaceIndex = cmdLine.indexOf(' ');
            if (spaceIndex == -1) {
                List<String> completions = new ArrayList<>();
                String prefix = (sender instanceof Player) ? "/" : "";
                for (Map.Entry<String, Command> entry : knownCommands.entrySet()) {
                    String name = entry.getKey();
                    Command command = entry.getValue();
                    if (RaspberryUtils.startsWithIgnoreCase(name, cmdLine)) {
                        if (!RaspberryBukkit.getBukkitInstance().isShowFallbackCommands()) {
                            if (name.contains(":")) {
                                continue;
                            }
                        }

                        if (command instanceof BukkitRaspberryCommand) {
                            RaspberryCommand executionNode = ((BukkitRaspberryCommand) command).getCommand().getChild(name);
                            if (executionNode == null) {
                                executionNode = ((BukkitRaspberryCommand) command).getCommand();
                            }
                            if (!executionNode.hasChildren()) {
                                RaspberryCommand testNode = executionNode.getChild(name);
                                if (testNode == null) {
                                    testNode = ((BukkitRaspberryCommand) command).getCommand().findChild(name);
                                }
                                if (testNode != null && !testNode.canUse(RaspberryBukkit.getBukkitInstance().getCommandIssuer(sender))) {
                                    continue;
                                }
                                if (testNode != null && testNode.isHidden()) {
                                    continue;
                                }
                            }
                        }

                        if (!command.testPermissionSilent(sender)) {
                            continue;
                        }
                        completions.add(prefix + name);
                    }
                }
                completions.sort(String.CASE_INSENSITIVE_ORDER);
                return completions;
            }
            String commandName = cmdLine.substring(0, spaceIndex);
            Command target = getCommand(commandName);
            if (target == null) {
                return null;
            }
            if (!target.testPermissionSilent(sender)) {
                return null;
            }
            String argLine = cmdLine.substring(spaceIndex + 1);
            String[] args = argLine.split(" ");
            try {
                List<String> completions = (target instanceof BukkitRaspberryCommand)
                        ? ((BukkitRaspberryCommand) target).tabComplete(sender, commandName, cmdLine)
                        : target.tabComplete(sender, commandName, args);

                if (completions != null) {
                    RaspberryUtils.performOnImmutable(completions, (list) -> {
                        list.addAll(completions);
                        list.sort(String.CASE_INSENSITIVE_ORDER);
                    });
                }
                return completions;
            } catch (CommandException ex) {
                throw ex;
            } catch (Throwable ex2) {
                throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, ex2);
            }
        }
    }
    @Override
    public void swap() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            if (commandMapField.get(Bukkit.getServer()) instanceof CommandMap) {
                return;
            }

            Object oldCommandMap = commandMapField.get(Bukkit.getServer());

            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            RaspberryUtils.setFinal(knownCommandsField, commandMap, knownCommandsField.get(oldCommandMap));
            RaspberryUtils.setFinal(commandMapField, Bukkit.getServer(), commandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
