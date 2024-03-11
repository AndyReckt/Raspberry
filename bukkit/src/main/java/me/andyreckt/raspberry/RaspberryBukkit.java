package me.andyreckt.raspberry;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.raspberry.adapter.defaults.BukkitTypeAdapters;
import me.andyreckt.raspberry.bukkit.BukkitRaspberryCommand;
import me.andyreckt.raspberry.bukkit.RaspberryCommandMap;
import me.andyreckt.raspberry.bukkit.completion.BukkitCommandCompletionContext;
import me.andyreckt.raspberry.command.*;
import me.andyreckt.raspberry.completions.CommandCompletionContext;
import me.andyreckt.raspberry.data.CommandData;
import me.andyreckt.raspberry.util.ClickablePart;
import me.andyreckt.raspberry.util.RaspberryBukkitConstant;
import me.andyreckt.raspberry.util.RaspberryUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;

public class RaspberryBukkit extends Raspberry {

    @Getter
    private static RaspberryBukkit bukkitInstance;
    private final JavaPlugin plugin;
    private final SimpleCommandMap commandMap;

    @Getter @Setter
    private boolean forceRegister = false;
    @Getter @Setter
    private boolean isShowFallbackCommands = false;

    public RaspberryBukkit(JavaPlugin plugin) {
        super(new RaspberryBukkitCommand());
        bukkitInstance = this;
        this.plugin = plugin;

        this.swapCommandMap();
        this.commandMap = getCommandMap();

        this.registerTypeAdapter(Player.class, BukkitTypeAdapters.PLAYER);
        this.registerTypeAdapter(OfflinePlayer.class, BukkitTypeAdapters.OFFLINE_PLAYER);
        this.registerTypeAdapter(World.class, BukkitTypeAdapters.WORLD);
        this.registerTypeAdapter(Material.class, BukkitTypeAdapters.MATERIAL);
        this.registerTypeAdapter(GameMode.class, BukkitTypeAdapters.GAMEMODE);
    }

    @Override
    public boolean isCommandIssuer(Class<?> issuer) {
        return CommandSender.class.isAssignableFrom(issuer);
    }

    @Override
    public void injectCommand(RaspberryCommand command) {
        if (commandMap.getCommand(command.getName()) != null && forceRegister) {
            logger.warning("Command " + command.getName() + " is already registered forcing the new one over it.");
            commandMap.getCommand(command.getName()).unregister(commandMap);
        }

        commandMap.register(plugin.getName(), new BukkitRaspberryCommand((RaspberryBukkitCommand) command, plugin));
    }

    @Override
    public CommandIssuer<?> getCommandIssuer(Object issuer) {
        if (!(issuer instanceof CommandSender))
            throw new IllegalArgumentException("Issuer must be an instance of CommandSender.");

        return new BukkitCommandIssuer((CommandSender) issuer);
    }

    @Override
    public CommandCompletionContext getCommandCompletionContext(RaspberryCommand command, CommandIssuer<?> issuer, String input) {
        return new BukkitCommandCompletionContext((RaspberryBukkitCommand) command, input, (BukkitCommandIssuer) issuer);
    }

    @Override
    public void sendClickable(CommandIssuer<?> issuer, List<ClickablePart> parts) {
        BukkitCommandIssuer bukkitIssuer = (BukkitCommandIssuer) issuer;
        bukkitIssuer.sendClickable(parts);
    }

    @Override @SuppressWarnings("unchecked")
    public RaspberryBukkitCommand createCommand(CommandData data) {
        return new RaspberryBukkitCommand(data);
    }

    @Override
    public String getDefaultUnknownCommandMessage() {
        return RaspberryBukkitConstant.UNKNOWN_COMMAND_MESSAGE;
    }

    private SimpleCommandMap getCommandMap() {
        try {
            SimplePluginManager pluginManager = (SimplePluginManager) Bukkit.getPluginManager();

            Field field = pluginManager.getClass().getDeclaredField("commandMap");
            field.setAccessible(true);

            return (SimpleCommandMap) field.get(pluginManager);
        } catch (Exception exception) {
            throw new RuntimeException(exception);

        }
    }

    private void swapCommandMap() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            if (commandMapField.get(Bukkit.getServer()) instanceof RaspberryCommandMap) {
                return;
            }

            Object oldCommandMap = commandMapField.get(Bukkit.getServer());
            RaspberryCommandMap newCommandMap = new RaspberryCommandMap(Bukkit.getServer());

            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            RaspberryUtils.setFinal(knownCommandsField, newCommandMap, knownCommandsField.get(oldCommandMap));
            RaspberryUtils.setFinal(commandMapField, Bukkit.getServer(), newCommandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public BukkitRaspberryCommand getBukkitCommand(String label) {
        if (label == null) {
            return null;
        }

        Command command = commandMap.getCommand(label);

        if (command instanceof BukkitRaspberryCommand) {
            return (BukkitRaspberryCommand) command;
        }

        return null;
    }

}