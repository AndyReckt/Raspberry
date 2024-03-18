package me.andyreckt.raspberry;

import lombok.Getter;
import me.andyreckt.raspberry.adapter.defaults.BungeeTypeAdapters;
import me.andyreckt.raspberry.bungee.BungeeRaspberryCommand;
import me.andyreckt.raspberry.bungee.completion.BungeeCommandCompletionContext;
import me.andyreckt.raspberry.command.BungeeCommandIssuer;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryBungeeCommand;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.completions.CommandCompletionContext;
import me.andyreckt.raspberry.data.CommandData;
import me.andyreckt.raspberry.exception.ConditionFailedException;
import me.andyreckt.raspberry.util.ClickablePart;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;
import java.util.function.Consumer;

public class RaspberryBungee extends Raspberry {

    @Getter
    private static RaspberryBungee bungeeInstance;

    private final Plugin plugin;

    public RaspberryBungee(Plugin plugin) {
        super(new RaspberryBungeeCommand());
        bungeeInstance = this;
        this.plugin = plugin;

        this.registerTypeAdapter(ProxiedPlayer.class, BungeeTypeAdapters.PROXIED_PLAYER);
        this.registerTypeAdapter(ServerInfo.class, BungeeTypeAdapters.SERVER_INFO);
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

    /**
     * Register a command condition.
     * (You should throw a {@link ConditionFailedException}
     * if the condition is not met.)
     *
     * @param id the id of the condition.
     * @param condition the condition to register.
     */
    public void registerCondition(String id, Consumer<BungeeCommandIssuer> condition) {
        this.getCommandHandler().registerCondition(id, condition);
    }

    @Override
    public CommandCompletionContext getCommandCompletionContext(RaspberryCommand command, CommandIssuer<?> issuer, String input) {
        return new BungeeCommandCompletionContext((RaspberryBungeeCommand) command, input, (BungeeCommandIssuer) issuer);
    }

    @Override
    public void sendClickable(CommandIssuer<?> issuer, List<ClickablePart> parts) {
        BungeeCommandIssuer bungeeIssuer = (BungeeCommandIssuer) issuer;
        bungeeIssuer.sendClickable(parts);
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