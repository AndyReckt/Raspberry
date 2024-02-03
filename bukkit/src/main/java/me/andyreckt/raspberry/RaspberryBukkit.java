package me.andyreckt.raspberry;

import lombok.Getter;
import me.andyreckt.raspberry.adapter.defaults.BukkitTypeAdapters;
import me.andyreckt.raspberry.bukkit.BukkitRaspberryCommand;
import me.andyreckt.raspberry.bukkit.completion.BukkitCommandCompletionContext;
import me.andyreckt.raspberry.command.*;
import me.andyreckt.raspberry.completions.CommandCompletionAction;
import me.andyreckt.raspberry.completions.CommandCompletionContext;
import me.andyreckt.raspberry.data.CommandData;
import me.andyreckt.raspberry.data.IData;
import me.andyreckt.raspberry.util.RaspberryBukkitConstant;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RaspberryBukkit extends Raspberry {

    @Getter
    private static RaspberryBukkit bukkitInstance;
    private final JavaPlugin plugin;
    private final SimpleCommandMap commandMap;

    public RaspberryBukkit(JavaPlugin plugin) {
        super(new RaspberryBukkitCommand());
        bukkitInstance = this;
        this.plugin = plugin;

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
        RaspberryBukkitCommand bukkitCommand = (RaspberryBukkitCommand) command;
        commandMap.register(plugin.getName(), new BukkitRaspberryCommand(bukkitCommand, plugin));
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