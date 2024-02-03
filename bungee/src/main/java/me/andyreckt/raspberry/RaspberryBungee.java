package me.andyreckt.raspberry;

import lombok.Getter;
import me.andyreckt.raspberry.bungee.BungeeRaspberryCommand;
import me.andyreckt.raspberry.bungee.completion.BungeeCommandCompletionContext;
import me.andyreckt.raspberry.command.BungeeCommandIssuer;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryBungeeCommand;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.completions.CommandCompletionContext;
import me.andyreckt.raspberry.data.CommandData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;

public class RaspberryBungee extends Raspberry {

    @Getter
    private static RaspberryBungee bungeeInstance;

    private final Plugin plugin;

    public RaspberryBungee(Plugin plugin) {
        super(new RaspberryBungeeCommand());
        bungeeInstance = this;
        this.plugin = plugin;
    }

    @Override
    public void injectCommand(RaspberryCommand command) {
        plugin.getProxy().getPluginManager().registerCommand(plugin, new BungeeRaspberryCommand(command));
    }

    @Override
    public boolean isCommandIssuer(Class<?> issuer) {
        return CommandSender.class.isAssignableFrom(issuer);
    }

    @Override
    public CommandIssuer<?> getCommandIssuer(Object issuer) {
        if (!(issuer instanceof CommandSender)) {
            throw new IllegalArgumentException("Issuer must be a CommandSender");
        }

        return new BungeeCommandIssuer((CommandSender) issuer);
    }

    @Override
    public CommandCompletionContext getCommandCompletionContext(RaspberryCommand command, CommandIssuer<?> issuer, String input) {
        return new BungeeCommandCompletionContext((RaspberryBungeeCommand) command, input, (BungeeCommandIssuer) issuer);
    }

    @Override
    public RaspberryBungeeCommand createCommand(CommandData data) {
        return new RaspberryBungeeCommand(data);
    }

    @Override
    public String getDefaultUnknownCommandMessage() {
        return "Unknown command. Type \"/help\" for help.";
    }
}